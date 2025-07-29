package erp.core.repository;

import erp.core.entity.*;
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

    //region 依賴注入
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private SlipRepository slipRepository;

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    //endregion

    //region ItemRepository 測試
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
    //endregion

    //region StockRepository 測試
    @Test
    public void testStockRepository() {
        // 創建測試數據
        Stock stock = new Stock();
        stock.setItemId(1L);
        stock.setWarehouseId(1L);
        stock.setStorageLocationId(1L);
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
        
        // 測試按倉庫ID查找
        List<Stock> stocksByWarehouseId = stockRepository.findByWarehouseId(1L);
        assertThat(stocksByWarehouseId).hasSize(1);
        
        // 測試按儲位ID查找
        List<Stock> stocksByStorageLocationId = stockRepository.findByStorageLocationId(1L);
        assertThat(stocksByStorageLocationId).hasSize(1);
        
        // 測試按商品ID和儲位ID查找
        Optional<Stock> stockByItemAndStorageLocation = stockRepository.findByItemIdAndStorageLocationId(1L, 1L);
        assertThat(stockByItemAndStorageLocation).isPresent();
        
        // 測試總量計算
        BigDecimal totalQuantity = stockRepository.getTotalQuantityByItemId(1L);
        assertThat(totalQuantity).isEqualByComparingTo(new BigDecimal("100.00"));
    }
    //endregion

    //region StockMovementRepository 測試
    @Test
    public void testStockMovementRepository() {
        // 創建測試數據
        StockMovement movement = new StockMovement();
        movement.setItemId(1L);
        movement.setWarehouseId(1L);
        movement.setStorageLocationId(1L);
        movement.setSlipId(1L);
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
        
        // 測試按倉庫ID查找
        List<StockMovement> movementsByWarehouseId = stockMovementRepository.findByWarehouseId(1L);
        assertThat(movementsByWarehouseId).hasSize(1);
        
        // 測試按儲位ID查找
        List<StockMovement> movementsByStorageLocationId = stockMovementRepository.findByStorageLocationId(1L);
        assertThat(movementsByStorageLocationId).hasSize(1);
        
        // 測試按單據ID查找
        List<StockMovement> movementsBySlipId = stockMovementRepository.findBySlipId(1L);
        assertThat(movementsBySlipId).hasSize(1);
        
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
    //endregion

    //region SlipRepository 測試
    @Test
    public void testSlipRepository() {
        // 創建測試數據
        Slip slip = new Slip();
        slip.setSlipsType((byte) 1); // 入庫單
        slip.setCreatedBy(1L);
        slip.setStatus((byte) 0); // 草稿
        
        // 保存並刷新
        Slip savedSlip = slipRepository.save(slip);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Slip> foundSlip = slipRepository.findById(savedSlip.getId());
        assertThat(foundSlip).isPresent();
        assertThat(foundSlip.get().getSlipsType()).isEqualTo((byte) 1);
        
        // 測試按單據類型查找
        List<Slip> slipsByType = slipRepository.findBySlipsType((byte) 1);
        assertThat(slipsByType).hasSize(1);
        
        // 測試按建立人查找
        List<Slip> slipsByCreatedBy = slipRepository.findByCreatedBy(1L);
        assertThat(slipsByCreatedBy).hasSize(1);
        
        // 測試按狀態查找
        List<Slip> slipsByStatus = slipRepository.findByStatus((byte) 0);
        assertThat(slipsByStatus).hasSize(1);
        
        // 測試統計功能
        Long count = slipRepository.countBySlipsTypeAndStatus((byte) 1, (byte) 0);
        assertThat(count).isEqualTo(1L);
    }
    //endregion

    //region StorageLocationRepository 測試
    @Test
    public void testStorageLocationRepository() {
        // 創建測試數據
        StorageLocation location = new StorageLocation();
        location.setWarehouseId(1L);
        location.setCode("A01-B02");
        location.setZone("A區");
        location.setCapacity(100);
        location.setUnit("箱");
        location.setStatus((byte) 1); // 啟用
        
        // 保存並刷新
        StorageLocation savedLocation = storageLocationRepository.save(location);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<StorageLocation> foundLocation = storageLocationRepository.findById(savedLocation.getId());
        assertThat(foundLocation).isPresent();
        assertThat(foundLocation.get().getCode()).isEqualTo("A01-B02");
        
        // 測試按倉庫ID查找
        List<StorageLocation> locationsByWarehouse = storageLocationRepository.findByWarehouseId(1L);
        assertThat(locationsByWarehouse).hasSize(1);
        
        // 測試按編號查找
        Optional<StorageLocation> locationByCode = storageLocationRepository.findByCode("A01-B02");
        assertThat(locationByCode).isPresent();
        
        // 測試按狀態查找
        List<StorageLocation> activeLocations = storageLocationRepository.findByStatus((byte) 1);
        assertThat(activeLocations).hasSize(1);
        
        // 測試統計功能
        Long totalCapacity = storageLocationRepository.getTotalCapacityByWarehouseId(1L);
        assertThat(totalCapacity).isEqualTo(100L);
    }
    //endregion

    //region UserRepository 測試
    @Test
    public void testUserRepository() {
        // 創建測試數據
        User user = new User();
        user.setName("測試使用者");
        user.setRole("倉管");
        user.setStatus((byte) 1); // 啟用
        
        // 保存並刷新
        User savedUser = userRepository.save(user);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("測試使用者");
        
        // 測試按姓名查找
        Optional<User> userByName = userRepository.findByName("測試使用者");
        assertThat(userByName).isPresent();
        
        // 測試按角色查找
        List<User> usersByRole = userRepository.findByRole("倉管");
        assertThat(usersByRole).hasSize(1);
        
        // 測試按狀態查找
        List<User> activeUsers = userRepository.findByStatus((byte) 1);
        assertThat(activeUsers).hasSize(1);
        
        // 測試統計功能
        Long activeUserCount = userRepository.countActiveUsers();
        assertThat(activeUserCount).isEqualTo(1L);
    }
    //endregion

    //region WarehouseRepository 測試
    @Test
    public void testWarehouseRepository() {
        // 創建測試數據
        Warehouse warehouse = new Warehouse();
        warehouse.setName("測試倉庫");
        warehouse.setType("常溫");
        warehouse.setLocation("台北市");
        warehouse.setAreaM2(new BigDecimal("1000.50"));
        warehouse.setStatus((byte) 1); // 啟用
        
        // 保存並刷新
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<Warehouse> foundWarehouse = warehouseRepository.findById(savedWarehouse.getId());
        assertThat(foundWarehouse).isPresent();
        assertThat(foundWarehouse.get().getName()).isEqualTo("測試倉庫");
        
        // 測試按名稱查找
        Optional<Warehouse> warehouseByName = warehouseRepository.findByName("測試倉庫");
        assertThat(warehouseByName).isPresent();
        
        // 測試按類型查找
        List<Warehouse> warehousesByType = warehouseRepository.findByType("常溫");
        assertThat(warehousesByType).hasSize(1);
        
        // 測試按狀態查找
        List<Warehouse> activeWarehouses = warehouseRepository.findByStatus((byte) 1);
        assertThat(activeWarehouses).hasSize(1);
        
        // 測試統計功能
        BigDecimal totalArea = warehouseRepository.getTotalActiveWarehouseArea();
        assertThat(totalArea).isEqualByComparingTo(new BigDecimal("1000.50"));
    }
    //endregion

    //region 整合測試
    @Test
    public void testRepositoryIntegration() {
        // 測試所有repository都能正常注入
        assertThat(itemRepository).isNotNull();
        assertThat(stockRepository).isNotNull();
        assertThat(stockMovementRepository).isNotNull();
        assertThat(slipRepository).isNotNull();
        assertThat(storageLocationRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(warehouseRepository).isNotNull();
        
        // 測試基本的count操作
        long itemCount = itemRepository.count();
        long stockCount = stockRepository.count();
        long movementCount = stockMovementRepository.count();
        long slipCount = slipRepository.count();
        long storageLocationCount = storageLocationRepository.count();
        long userCount = userRepository.count();
        long warehouseCount = warehouseRepository.count();
        
        assertThat(itemCount).isGreaterThanOrEqualTo(0);
        assertThat(stockCount).isGreaterThanOrEqualTo(0);
        assertThat(movementCount).isGreaterThanOrEqualTo(0);
        assertThat(slipCount).isGreaterThanOrEqualTo(0);
        assertThat(storageLocationCount).isGreaterThanOrEqualTo(0);
        assertThat(userCount).isGreaterThanOrEqualTo(0);
        assertThat(warehouseCount).isGreaterThanOrEqualTo(0);
    }
    //endregion
}