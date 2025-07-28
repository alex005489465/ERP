package erp.core.repository;

import erp.core.entity.Item;
import erp.core.entity.Stock;
import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Test
    public void testItemRepository() {
        // 創建測試數據
        Item item = new Item();
        item.setName("測試商品");
        item.setUnit("個");
        
        // 保存並刷新
        Item savedItem = itemRepository.save(item);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Item> foundItem = itemRepository.findById(savedItem.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("測試商品");
        
        // 測試按名稱查找
        Optional<Item> itemByName = itemRepository.findByName("測試商品");
        assertThat(itemByName).isPresent();
        
        // 測試存在性檢查
        boolean exists = itemRepository.existsByName("測試商品");
        assertThat(exists).isTrue();
        
        // 測試模糊查詢
        List<Item> itemsContaining = itemRepository.findByNameContainingIgnoreCase("測試");
        assertThat(itemsContaining).hasSize(1);
    }

    @Test
    public void testStockRepository() {
        // 創建測試數據
        Stock stock = new Stock();
        stock.setItemId(1L);
        stock.setLocation("倉庫A");
        stock.setQuantity(new BigDecimal("100.00"));
        
        // 保存並刷新
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualByComparingTo(new BigDecimal("100.00"));
        
        // 測試按商品ID查找
        List<Stock> stocksByItemId = stockRepository.findByItemId(1L);
        assertThat(stocksByItemId).hasSize(1);
        
        // 測試按位置查找
        List<Stock> stocksByLocation = stockRepository.findByLocation("倉庫A");
        assertThat(stocksByLocation).hasSize(1);
        
        // 測試按商品ID和位置查找
        Optional<Stock> stockByItemAndLocation = stockRepository.findByItemIdAndLocation(1L, "倉庫A");
        assertThat(stockByItemAndLocation).isPresent();
        
        // 測試總量計算
        BigDecimal totalQuantity = stockRepository.getTotalQuantityByItemId(1L);
        assertThat(totalQuantity).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    public void testStockMovementRepository() {
        // 創建測試數據
        StockMovement movement = new StockMovement();
        movement.setItemId(1L);
        movement.setLocation("倉庫A");
        movement.setType(MovementType.INBOUND); // 入庫
        movement.setQuantityChange(new BigDecimal("50.00"));
        movement.setNote("測試入庫");
        
        // 保存並刷新
        StockMovement savedMovement = stockMovementRepository.save(movement);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<StockMovement> foundMovement = stockMovementRepository.findById(savedMovement.getId());
        assertThat(foundMovement).isPresent();
        assertThat(foundMovement.get().getType()).isEqualTo(MovementType.INBOUND);
        
        // 測試按商品ID查找
        List<StockMovement> movementsByItemId = stockMovementRepository.findByItemId(1L);
        assertThat(movementsByItemId).hasSize(1);
        
        // 測試按類型查找
        List<StockMovement> inboundMovements = stockMovementRepository.findByType(MovementType.INBOUND);
        assertThat(inboundMovements).hasSize(1);
        
        // 測試入庫記錄查詢
        List<StockMovement> inboundByItemId = stockMovementRepository.findInboundMovementsByItemId(1L, MovementType.INBOUND);
        assertThat(inboundByItemId).hasSize(1);
        
        // 測試總入庫量計算
        BigDecimal totalInbound = stockMovementRepository.getTotalInboundQuantityByItemId(1L, MovementType.INBOUND);
        assertThat(totalInbound).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    public void testRepositoryIntegration() {
        // 測試所有repository都能正常注入
        assertThat(itemRepository).isNotNull();
        assertThat(stockRepository).isNotNull();
        assertThat(stockMovementRepository).isNotNull();
        
        // 測試基本的count操作
        long itemCount = itemRepository.count();
        long stockCount = stockRepository.count();
        long movementCount = stockMovementRepository.count();
        
        assertThat(itemCount).isGreaterThanOrEqualTo(0);
        assertThat(stockCount).isGreaterThanOrEqualTo(0);
        assertThat(movementCount).isGreaterThanOrEqualTo(0);
    }
}