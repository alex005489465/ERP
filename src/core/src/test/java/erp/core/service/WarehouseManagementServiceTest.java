package erp.core.service;

import erp.core.entity.Item;
import erp.core.entity.Stock;
import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import erp.core.repository.ItemRepository;
import erp.core.repository.StockRepository;
import erp.core.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WarehouseManagementServiceTest {

    //region 依賴注入與測試常量
    @Autowired
    private WarehouseManagementService warehouseService;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    private Item testItem;
    private final String TEST_LOCATION_A = "WH_A";
    private final String TEST_LOCATION_B = "WH_B";
    //endregion
    
    //region 測試設置
    @BeforeEach
    void setUp() {
        // 清理測試數據
        stockMovementRepository.deleteAll();
        stockRepository.deleteAll();
        itemRepository.deleteAll();
        
        // 創建測試商品
        testItem = warehouseService.createItem("測試商品", "個");
        
        System.out.println("[DEBUG_LOG] 測試設置完成，創建商品ID: " + testItem.getId());
    }
    //endregion
    
    //region 商品管理測試
    
    @Test
    void testCreateItem() {
        Item item = warehouseService.createItem("新商品", "箱");
        
        assertNotNull(item.getId());
        assertEquals("新商品", item.getName());
        assertEquals("箱", item.getUnit());
        
        System.out.println("[DEBUG_LOG] 創建商品測試通過: " + item);
    }
    
    @Test
    void testCreateItemWithDuplicateName() {
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.createItem("測試商品", "個");
        });
        
        System.out.println("[DEBUG_LOG] 重複商品名稱測試通過");
    }
    
    @Test
    void testUpdateItem() {
        Item updatedItem = warehouseService.updateItem(testItem.getId(), "更新商品", "公斤");
        
        assertEquals("更新商品", updatedItem.getName());
        assertEquals("公斤", updatedItem.getUnit());
        
        System.out.println("[DEBUG_LOG] 更新商品測試通過: " + updatedItem);
    }
    
    @Test
    void testDeleteItem() {
        Long itemId = testItem.getId();
        warehouseService.deleteItem(itemId);
        
        assertFalse(itemRepository.existsById(itemId));
        
        System.out.println("[DEBUG_LOG] 刪除商品測試通過");
    }
    
    @Test
    void testDeleteItemWithStock() {
        // 先創建庫存
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(100), "測試入庫");
        
        // 嘗試刪除有庫存的商品應該失敗
        assertThrows(IllegalStateException.class, () -> {
            warehouseService.deleteItem(testItem.getId());
        });
        
        System.out.println("[DEBUG_LOG] 刪除有庫存商品測試通過");
    }
    //endregion
    
    //region 庫存操作測試
    
    @Test
    void testInbound() {
        BigDecimal quantity = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, quantity, "測試入庫");
        
        // 檢查庫存
        Optional<Stock> stock = warehouseService.getStock(testItem.getId(), TEST_LOCATION_A);
        assertTrue(stock.isPresent());
        assertEquals(0, quantity.compareTo(stock.get().getQuantity()));
        
        // 檢查異動記錄
        List<StockMovement> movements = warehouseService.getStockMovements(testItem.getId());
        assertEquals(1, movements.size());
        assertEquals(MovementType.INBOUND, movements.get(0).getType());
        
        System.out.println("[DEBUG_LOG] 入庫測試通過，庫存: " + stock.get().getQuantity());
    }
    
    @Test
    void testOutbound() {
        // 先入庫
        BigDecimal inboundQty = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, inboundQty, "測試入庫");
        
        // 再出庫
        BigDecimal outboundQty = BigDecimal.valueOf(30);
        warehouseService.outbound(testItem.getId(), TEST_LOCATION_A, outboundQty, "測試出庫");
        
        // 檢查庫存
        Optional<Stock> stock = warehouseService.getStock(testItem.getId(), TEST_LOCATION_A);
        assertTrue(stock.isPresent());
        assertEquals(0, BigDecimal.valueOf(70).compareTo(stock.get().getQuantity()));
        
        // 檢查異動記錄
        List<StockMovement> movements = warehouseService.getStockMovements(testItem.getId());
        assertEquals(2, movements.size());
        
        System.out.println("[DEBUG_LOG] 出庫測試通過，剩餘庫存: " + stock.get().getQuantity());
    }
    
    @Test
    void testOutboundInsufficientStock() {
        // 先入庫少量
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(10), "測試入庫");
        
        // 嘗試出庫超過庫存的數量
        assertThrows(IllegalStateException.class, () -> {
            warehouseService.outbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(20), "測試出庫");
        });
        
        System.out.println("[DEBUG_LOG] 庫存不足出庫測試通過");
    }
    
    @Test
    void testTransfer() {
        // 先在位置A入庫
        BigDecimal quantity = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, quantity, "測試入庫");
        
        // 轉庫到位置B
        BigDecimal transferQty = BigDecimal.valueOf(40);
        warehouseService.transfer(testItem.getId(), TEST_LOCATION_A, TEST_LOCATION_B, transferQty, "測試轉庫");
        
        // 檢查位置A庫存
        Optional<Stock> stockA = warehouseService.getStock(testItem.getId(), TEST_LOCATION_A);
        assertTrue(stockA.isPresent());
        assertEquals(0, BigDecimal.valueOf(60).compareTo(stockA.get().getQuantity()));
        
        // 檢查位置B庫存
        Optional<Stock> stockB = warehouseService.getStock(testItem.getId(), TEST_LOCATION_B);
        assertTrue(stockB.isPresent());
        assertEquals(0, transferQty.compareTo(stockB.get().getQuantity()));
        
        // 檢查異動記錄（應該有3筆：1筆入庫，2筆轉庫）
        List<StockMovement> movements = warehouseService.getStockMovements(testItem.getId());
        assertEquals(3, movements.size());
        
        System.out.println("[DEBUG_LOG] 轉庫測試通過，位置A庫存: " + stockA.get().getQuantity() + 
                          ", 位置B庫存: " + stockB.get().getQuantity());
    }
    
    @Test
    void testFreeze() {
        // 先入庫
        BigDecimal quantity = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, quantity, "測試入庫");
        
        // 凍結部分庫存
        BigDecimal freezeQty = BigDecimal.valueOf(30);
        warehouseService.freeze(testItem.getId(), TEST_LOCATION_A, freezeQty, "測試凍結");
        
        // 檢查原位置庫存
        Optional<Stock> stockA = warehouseService.getStock(testItem.getId(), TEST_LOCATION_A);
        assertTrue(stockA.isPresent());
        assertEquals(0, BigDecimal.valueOf(70).compareTo(stockA.get().getQuantity()));
        
        // 檢查凍結倉庫存
        Optional<Stock> freezeStock = warehouseService.getStock(testItem.getId(), WarehouseManagementService.FREEZE_WAREHOUSE);
        assertTrue(freezeStock.isPresent());
        assertEquals(0, freezeQty.compareTo(freezeStock.get().getQuantity()));
        
        System.out.println("[DEBUG_LOG] 凍結測試通過，原位置庫存: " + stockA.get().getQuantity() + 
                          ", 凍結倉庫存: " + freezeStock.get().getQuantity());
    }
    
    @Test
    void testScrap() {
        // 先入庫
        BigDecimal quantity = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, quantity, "測試入庫");
        
        // 報廢部分庫存
        BigDecimal scrapQty = BigDecimal.valueOf(20);
        warehouseService.scrap(testItem.getId(), TEST_LOCATION_A, scrapQty, "測試報廢");
        
        // 檢查原位置庫存
        Optional<Stock> stockA = warehouseService.getStock(testItem.getId(), TEST_LOCATION_A);
        assertTrue(stockA.isPresent());
        assertEquals(0, BigDecimal.valueOf(80).compareTo(stockA.get().getQuantity()));
        
        // 檢查報廢倉庫存
        Optional<Stock> scrapStock = warehouseService.getStock(testItem.getId(), WarehouseManagementService.SCRAP_WAREHOUSE);
        assertTrue(scrapStock.isPresent());
        assertEquals(0, scrapQty.compareTo(scrapStock.get().getQuantity()));
        
        System.out.println("[DEBUG_LOG] 報廢測試通過，原位置庫存: " + stockA.get().getQuantity() + 
                          ", 報廢倉庫存: " + scrapStock.get().getQuantity());
    }
    
    @Test
    void testUnfreeze() {
        // 先入庫並凍結
        BigDecimal quantity = BigDecimal.valueOf(100);
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, quantity, "測試入庫");
        
        BigDecimal freezeQty = BigDecimal.valueOf(50);
        warehouseService.freeze(testItem.getId(), TEST_LOCATION_A, freezeQty, "測試凍結");
        
        // 解凍到位置B
        BigDecimal unfreezeQty = BigDecimal.valueOf(20);
        warehouseService.unfreeze(testItem.getId(), TEST_LOCATION_B, unfreezeQty, "測試解凍");
        
        // 檢查凍結倉庫存
        Optional<Stock> freezeStock = warehouseService.getStock(testItem.getId(), WarehouseManagementService.FREEZE_WAREHOUSE);
        assertTrue(freezeStock.isPresent());
        assertEquals(0, BigDecimal.valueOf(30).compareTo(freezeStock.get().getQuantity()));
        
        // 檢查位置B庫存
        Optional<Stock> stockB = warehouseService.getStock(testItem.getId(), TEST_LOCATION_B);
        assertTrue(stockB.isPresent());
        assertEquals(0, unfreezeQty.compareTo(stockB.get().getQuantity()));
        
        System.out.println("[DEBUG_LOG] 解凍測試通過，凍結倉剩餘: " + freezeStock.get().getQuantity() + 
                          ", 位置B庫存: " + stockB.get().getQuantity());
    }
    //endregion
    
    //region 庫存查詢測試
    
    @Test
    void testGetTotalStock() {
        // 在多個位置入庫
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(100), "測試入庫A");
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_B, BigDecimal.valueOf(50), "測試入庫B");
        
        BigDecimal totalStock = warehouseService.getTotalStock(testItem.getId());
        assertEquals(0, BigDecimal.valueOf(150).compareTo(totalStock));
        
        System.out.println("[DEBUG_LOG] 總庫存查詢測試通過，總庫存: " + totalStock);
    }
    
    @Test
    void testGetStocksByItem() {
        // 在多個位置入庫
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(100), "測試入庫A");
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_B, BigDecimal.valueOf(50), "測試入庫B");
        
        List<Stock> stocks = warehouseService.getStocksByItem(testItem.getId());
        assertEquals(2, stocks.size());
        
        System.out.println("[DEBUG_LOG] 商品庫存查詢測試通過，庫存記錄數: " + stocks.size());
    }
    
    @Test
    void testGetStockMovements() {
        // 執行多種操作
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(100), "測試入庫");
        warehouseService.outbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(20), "測試出庫");
        warehouseService.transfer(testItem.getId(), TEST_LOCATION_A, TEST_LOCATION_B, BigDecimal.valueOf(30), "測試轉庫");
        
        List<StockMovement> movements = warehouseService.getStockMovements(testItem.getId());
        assertEquals(4, movements.size()); // 1入庫 + 1出庫 + 2轉庫
        
        System.out.println("[DEBUG_LOG] 庫存異動記錄查詢測試通過，異動記錄數: " + movements.size());
    }
    //endregion
    
    //region 邊界條件測試
    
    @Test
    void testInvalidQuantity() {
        // 測試負數數量
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(-10), "測試");
        });
        
        // 測試零數量
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.outbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.ZERO, "測試");
        });
        
        System.out.println("[DEBUG_LOG] 無效數量測試通過");
    }
    
    @Test
    void testNonExistentItem() {
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.inbound(99999L, TEST_LOCATION_A, BigDecimal.valueOf(10), "測試");
        });
        
        System.out.println("[DEBUG_LOG] 不存在商品測試通過");
    }
    
    @Test
    void testTransferSameLocation() {
        warehouseService.inbound(testItem.getId(), TEST_LOCATION_A, BigDecimal.valueOf(100), "測試入庫");
        
        assertThrows(IllegalArgumentException.class, () -> {
            warehouseService.transfer(testItem.getId(), TEST_LOCATION_A, TEST_LOCATION_A, BigDecimal.valueOf(10), "測試");
        });
        
        System.out.println("[DEBUG_LOG] 相同位置轉庫測試通過");
    }
    //endregion
}