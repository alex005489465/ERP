package erp.core.service;

import erp.core.entity.Item;
import erp.core.entity.Stock;
import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import erp.core.entity.StorageLocation;
import erp.core.repository.ItemRepository;
import erp.core.repository.StockRepository;
import erp.core.repository.StockMovementRepository;
import erp.core.repository.StorageLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 倉儲管理服務類
 * 包含商品管理、庫存查詢、庫存操作等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseManagementService {
    
    //region 常量定義與枚舉
    // 特殊倉庫常量定義
    public static final String FREEZE_WAREHOUSE = "FREEZE_WH";
    public static final String SCRAP_WAREHOUSE = "SCRAP_WH";
    
    /**
     * 庫存操作類型枚舉
     */
    public enum OperationType {
        INBOUND("入庫"),
        OUTBOUND("出庫"),
        TRANSFER("轉庫"),
        FREEZE("凍結"),
        SCRAP("報廢");
        
        private final String description;
        
        OperationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    //endregion
    
    //region 依賴注入
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StorageLocationRepository storageLocationRepository;
    //endregion
    
    //region 商品管理 (CRUD)
    /**
     * 新增商品
     */
    @Transactional
    public Item createItem(String name, String unit) {
        if (itemRepository.existsByName(name)) {
            throw new IllegalArgumentException("商品名稱已存在: " + name);
        }
        
        Item item = new Item();
        item.setName(name);
        item.setUnit(unit);
        
        Item savedItem = itemRepository.save(item);
        log.info("新增商品成功: {}", savedItem);
        return savedItem;
    }
    
    /**
     * 更新商品
     */
    @Transactional
    public Item updateItem(Long itemId, String name, String unit) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在: " + itemId));
        
        // 檢查名稱是否與其他商品重複
        Optional<Item> existingItem = itemRepository.findByName(name);
        if (existingItem.isPresent() && !existingItem.get().getId().equals(itemId)) {
            throw new IllegalArgumentException("商品名稱已存在: " + name);
        }
        
        item.setName(name);
        item.setUnit(unit);
        
        Item updatedItem = itemRepository.save(item);
        log.info("更新商品成功: {}", updatedItem);
        return updatedItem;
    }
    
    /**
     * 刪除商品
     */
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("商品不存在: " + itemId);
        }
        
        // 檢查是否有庫存記錄
        List<Stock> stocks = stockRepository.findByItemId(itemId);
        if (!stocks.isEmpty()) {
            throw new IllegalStateException("無法刪除有庫存記錄的商品: " + itemId);
        }
        
        itemRepository.deleteById(itemId);
        log.info("刪除商品成功: {}", itemId);
    }
    
    /**
     * 查詢商品
     */
    public Optional<Item> getItem(Long itemId) {
        return itemRepository.findById(itemId);
    }
    
    /**
     * 查詢所有商品
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    /**
     * 根據名稱模糊查詢商品
     */
    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }
    //endregion
    
    //region 庫存查詢
    /**
     * 查詢指定商品的所有庫存
     */
    public List<Stock> getStocksByItem(Long itemId) {
        return stockRepository.findByItemId(itemId);
    }
    
    /**
     * 查詢指定位置的所有庫存
     */
    public List<Stock> getStocksByLocation(String location) {
        StorageLocation storageLocation = getStorageLocationByCode(location);
        return stockRepository.findByStorageLocationId(storageLocation.getId());
    }
    
    /**
     * 查詢指定商品在指定位置的庫存
     */
    public Optional<Stock> getStock(Long itemId, String location) {
        StorageLocation storageLocation = getStorageLocationByCode(location);
        return stockRepository.findByItemIdAndStorageLocationId(itemId, storageLocation.getId());
    }
    
    /**
     * 查詢指定商品的總庫存量
     */
    public BigDecimal getTotalStock(Long itemId) {
        return stockRepository.getTotalQuantityByItemId(itemId);
    }
    
    /**
     * 查詢所有庫存位置
     */
    public List<String> getAllLocations() {
        return stockRepository.findAllDistinctLocations();
    }
    
    /**
     * 查詢低庫存商品
     */
    public List<Stock> getLowStocks(BigDecimal threshold) {
        return stockRepository.findByQuantityLessThan(threshold);
    }
    
    /**
     * 查詢零庫存商品
     */
    public List<Stock> getZeroStocks() {
        return stockRepository.findZeroStocks();
    }
    //endregion
    
    //region 庫存操作
    /**
     * 入庫操作
     * @param itemId 商品ID
     * @param location 位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void inbound(Long itemId, String location, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("入庫數量必須大於0");
        }
        
        validateItemExists(itemId);
        
        // 執行入庫操作
        performStockOperation(itemId, location, MovementType.INBOUND, quantity, note);
        
        log.info("入庫操作完成 - 商品ID: {}, 位置: {}, 數量: {}", itemId, location, quantity);
    }
    
    /**
     * 出庫操作
     * @param itemId 商品ID
     * @param location 位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void outbound(Long itemId, String location, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("出庫數量必須大於0");
        }
        
        validateItemExists(itemId);
        
        // 檢查庫存是否足夠
        Optional<Stock> stockOpt = getStock(itemId, location);
        if (stockOpt.isEmpty() || stockOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("庫存不足，無法出庫");
        }
        
        // 執行出庫操作
        performStockOperation(itemId, location, MovementType.OUTBOUND, quantity, note);
        
        log.info("出庫操作完成 - 商品ID: {}, 位置: {}, 數量: {}", itemId, location, quantity);
    }
    
    /**
     * 轉庫操作 (從一個位置轉移到另一個位置)
     * @param itemId 商品ID
     * @param fromLocation 來源位置
     * @param toLocation 目標位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void transfer(Long itemId, String fromLocation, String toLocation, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("轉庫數量必須大於0");
        }
        
        if (fromLocation.equals(toLocation)) {
            throw new IllegalArgumentException("來源位置和目標位置不能相同");
        }
        
        validateItemExists(itemId);
        
        // 檢查來源庫存是否足夠
        Optional<Stock> fromStockOpt = getStock(itemId, fromLocation);
        if (fromStockOpt.isEmpty() || fromStockOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("來源位置庫存不足，無法轉庫");
        }
        
        String transferNote = String.format("轉庫: %s -> %s, %s", fromLocation, toLocation, note != null ? note : "");
        
        // 執行轉庫操作：先出庫，再入庫
        performStockOperation(itemId, fromLocation, MovementType.OUTBOUND, quantity, transferNote);
        performStockOperation(itemId, toLocation, MovementType.INBOUND, quantity, transferNote);
        
        log.info("轉庫操作完成 - 商品ID: {}, 從 {} 轉至 {}, 數量: {}", itemId, fromLocation, toLocation, quantity);
    }
    
    /**
     * 凍結操作 (將庫存轉移到凍結倉)
     * @param itemId 商品ID
     * @param fromLocation 來源位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void freeze(Long itemId, String fromLocation, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("凍結數量必須大於0");
        }
        
        validateItemExists(itemId);
        
        // 檢查來源庫存是否足夠
        Optional<Stock> fromStockOpt = getStock(itemId, fromLocation);
        if (fromStockOpt.isEmpty() || fromStockOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("來源位置庫存不足，無法凍結");
        }
        
        String freezeNote = String.format("凍結: %s -> %s, %s", fromLocation, FREEZE_WAREHOUSE, note != null ? note : "");
        
        // 執行凍結操作：從來源位置出庫，入庫到凍結倉
        performStockOperation(itemId, fromLocation, MovementType.OUTBOUND, quantity, freezeNote);
        performStockOperation(itemId, FREEZE_WAREHOUSE, MovementType.INBOUND, quantity, freezeNote);
        
        log.info("凍結操作完成 - 商品ID: {}, 從 {} 凍結至 {}, 數量: {}", itemId, fromLocation, FREEZE_WAREHOUSE, quantity);
    }
    
    /**
     * 報廢操作 (將庫存轉移到報廢倉)
     * @param itemId 商品ID
     * @param fromLocation 來源位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void scrap(Long itemId, String fromLocation, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("報廢數量必須大於0");
        }
        
        validateItemExists(itemId);
        
        // 檢查來源庫存是否足夠
        Optional<Stock> fromStockOpt = getStock(itemId, fromLocation);
        if (fromStockOpt.isEmpty() || fromStockOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("來源位置庫存不足，無法報廢");
        }
        
        String scrapNote = String.format("報廢: %s -> %s, %s", fromLocation, SCRAP_WAREHOUSE, note != null ? note : "");
        
        // 執行報廢操作：從來源位置出庫，入庫到報廢倉
        performStockOperation(itemId, fromLocation, MovementType.OUTBOUND, quantity, scrapNote);
        performStockOperation(itemId, SCRAP_WAREHOUSE, MovementType.INBOUND, quantity, scrapNote);
        
        log.info("報廢操作完成 - 商品ID: {}, 從 {} 報廢至 {}, 數量: {}", itemId, fromLocation, SCRAP_WAREHOUSE, quantity);
    }
    
    /**
     * 解凍操作 (將庫存從凍結倉轉移到指定位置)
     * @param itemId 商品ID
     * @param toLocation 目標位置
     * @param quantity 數量
     * @param note 備註
     */
    @Transactional
    public void unfreeze(Long itemId, String toLocation, BigDecimal quantity, String note) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("解凍數量必須大於0");
        }
        
        validateItemExists(itemId);
        
        // 檢查凍結倉庫存是否足夠
        Optional<Stock> freezeStockOpt = getStock(itemId, FREEZE_WAREHOUSE);
        if (freezeStockOpt.isEmpty() || freezeStockOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("凍結倉庫存不足，無法解凍");
        }
        
        String unfreezeNote = String.format("解凍: %s -> %s, %s", FREEZE_WAREHOUSE, toLocation, note != null ? note : "");
        
        // 執行解凍操作：從凍結倉出庫，入庫到目標位置
        performStockOperation(itemId, FREEZE_WAREHOUSE, MovementType.OUTBOUND, quantity, unfreezeNote);
        performStockOperation(itemId, toLocation, MovementType.INBOUND, quantity, unfreezeNote);
        
        log.info("解凍操作完成 - 商品ID: {}, 從 {} 解凍至 {}, 數量: {}", itemId, FREEZE_WAREHOUSE, toLocation, quantity);
    }
    //endregion
    
    //region 私有輔助方法
    /**
     * 執行庫存操作的核心方法
     * 按照事務要求：1. 開始事務 2. 查詢&更新庫存 3. 寫入異動記錄 4. 提交或回滾
     */
    private void performStockOperation(Long itemId, String location, MovementType movementType, 
                                     BigDecimal quantity, String note) {
        
        // 獲取儲位信息
        StorageLocation storageLocation = getStorageLocationByCode(location);
        Long warehouseId = storageLocation.getWarehouseId();
        Long storageLocationId = storageLocation.getId();
        
        // 1. 查詢現有庫存
        Optional<Stock> stockOpt = stockRepository.findByItemIdAndStorageLocationId(itemId, storageLocationId);
        Stock stock;
        
        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
        } else {
            // 如果庫存記錄不存在，創建新記錄
            stock = new Stock();
            stock.setItemId(itemId);
            stock.setWarehouseId(warehouseId);
            stock.setStorageLocationId(storageLocationId);
            stock.setQuantity(BigDecimal.ZERO);
        }
        
        // 2. 更新庫存數量
        BigDecimal newQuantity;
        if (movementType == MovementType.INBOUND) {
            newQuantity = stock.getQuantity().add(quantity);
        } else { // OUTBOUND
            newQuantity = stock.getQuantity().subtract(quantity);
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("庫存不足，操作後庫存將為負數");
            }
        }
        
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);
        
        // 3. 寫入庫存異動記錄
        StockMovement movement = new StockMovement();
        movement.setItemId(itemId);
        movement.setWarehouseId(warehouseId);
        movement.setStorageLocationId(storageLocationId);
        movement.setType(movementType);
        movement.setQuantityChange(quantity);
        movement.setNote(note);
        
        stockMovementRepository.save(movement);
        
        log.debug("庫存操作完成 - 商品ID: {}, 位置: {}, 類型: {}, 數量: {}, 新庫存: {}", 
                 itemId, location, movementType, quantity, newQuantity);
    }
    
    /**
     * 驗證商品是否存在
     */
    private void validateItemExists(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("商品不存在: " + itemId);
        }
    }
    
    /**
     * 根據位置編號獲取儲位信息
     * @param locationCode 位置編號 (如 "A001")
     * @return StorageLocation 儲位信息
     */
    private StorageLocation getStorageLocationByCode(String locationCode) {
        return storageLocationRepository.findByCode(locationCode)
            .orElseThrow(() -> new IllegalArgumentException("儲位不存在: " + locationCode));
    }
    //endregion
    
    //region 庫存異動記錄查詢
    /**
     * 查詢指定商品的庫存異動記錄
     */
    public List<StockMovement> getStockMovements(Long itemId) {
        return stockMovementRepository.findByItemId(itemId);
    }
    
    /**
     * 查詢指定位置的庫存異動記錄
     */
    public List<StockMovement> getStockMovementsByLocation(String location) {
        StorageLocation storageLocation = getStorageLocationByCode(location);
        return stockMovementRepository.findByStorageLocationId(storageLocation.getId());
    }
    
    /**
     * 查詢最近的庫存異動記錄
     */
    public List<StockMovement> getRecentStockMovements() {
        return stockMovementRepository.findTop10ByOrderByCreatedAtDesc();
    }
    //endregion
}