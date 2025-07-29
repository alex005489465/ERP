package erp.core.repository;

import erp.core.entity.*;
import erp.core.entity.StockMovement.MovementType;
import erp.core.repository.tests.*;
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

    @Autowired
    private SlipMovementRepository slipMovementRepository;

    @Autowired
    private SlipDetailRepository slipDetailRepository;
    //endregion

    //region 輔助方法
    private void setupItemRepositoryTest(ItemRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setItemRepository(itemRepository);
    }

    private void setupStockRepositoryTest(StockRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setStockRepository(stockRepository);
    }

    private void setupStockMovementRepositoryTest(StockMovementRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setStockMovementRepository(stockMovementRepository);
        test.setSlipMovementRepository(slipMovementRepository);
    }

    private void setupSlipRepositoryTest(SlipRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setSlipRepository(slipRepository);
    }

    private void setupSlipMovementRepositoryTest(SlipMovementRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setSlipMovementRepository(slipMovementRepository);
        test.setStockMovementRepository(stockMovementRepository);
    }

    private void setupStorageLocationRepositoryTest(StorageLocationRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setStorageLocationRepository(storageLocationRepository);
    }

    private void setupUserRepositoryTest(UserRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setUserRepository(userRepository);
    }

    private void setupWarehouseRepositoryTest(WarehouseRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setWarehouseRepository(warehouseRepository);
    }

    private void setupSlipDetailRepositoryTest(SlipDetailRepositoryTest test) {
        test.setEntityManager(entityManager);
        test.setSlipDetailRepository(slipDetailRepository);
    }
    //endregion

    //region ItemRepository 測試
    @Test
    public void testItemRepository() {
        // 委託給個別測試類
        ItemRepositoryTest itemTest = new ItemRepositoryTest();
        setupItemRepositoryTest(itemTest);
        itemTest.testItemRepository();
    }
    //endregion

    //region StockRepository 測試
    @Test
    public void testStockRepository() {
        // 委託給個別測試類
        StockRepositoryTest stockTest = new StockRepositoryTest();
        setupStockRepositoryTest(stockTest);
        stockTest.testStockRepository();
    }
    //endregion

    //region StockMovementRepository 測試
    @Test
    public void testStockMovementRepository() {
        // 委託給個別測試類
        StockMovementRepositoryTest stockMovementTest = new StockMovementRepositoryTest();
        setupStockMovementRepositoryTest(stockMovementTest);
        stockMovementTest.testStockMovementRepository();
    }
    //endregion

    //region SlipRepository 測試
    @Test
    public void testSlipRepository() {
        // 委託給個別測試類
        SlipRepositoryTest slipTest = new SlipRepositoryTest();
        setupSlipRepositoryTest(slipTest);
        slipTest.testSlipRepository();
    }
    //endregion

    //region SlipMovementRepository 測試
    @Test
    public void testSlipMovementRepository() {
        // 委託給個別測試類
        SlipMovementRepositoryTest slipMovementTest = new SlipMovementRepositoryTest();
        setupSlipMovementRepositoryTest(slipMovementTest);
        slipMovementTest.testSlipMovementRepository();
    }
    //endregion

    //region StorageLocationRepository 測試
    @Test
    public void testStorageLocationRepository() {
        // 委託給個別測試類
        StorageLocationRepositoryTest storageLocationTest = new StorageLocationRepositoryTest();
        setupStorageLocationRepositoryTest(storageLocationTest);
        storageLocationTest.testStorageLocationRepository();
    }
    //endregion

    //region UserRepository 測試
    @Test
    public void testUserRepository() {
        // 委託給個別測試類
        UserRepositoryTest userTest = new UserRepositoryTest();
        setupUserRepositoryTest(userTest);
        userTest.testUserRepository();
    }
    //endregion

    //region WarehouseRepository 測試
    @Test
    public void testWarehouseRepository() {
        // 委託給個別測試類
        WarehouseRepositoryTest warehouseTest = new WarehouseRepositoryTest();
        setupWarehouseRepositoryTest(warehouseTest);
        warehouseTest.testWarehouseRepository();
    }
    //endregion

    //region SlipDetailRepository 測試
    @Test
    public void testSlipDetailRepository() {
        // 委託給個別測試類
        SlipDetailRepositoryTest slipDetailTest = new SlipDetailRepositoryTest();
        setupSlipDetailRepositoryTest(slipDetailTest);
        slipDetailTest.testSlipDetailRepository();
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
        assertThat(slipDetailRepository).isNotNull();
        assertThat(storageLocationRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(warehouseRepository).isNotNull();
        
        // 測試基本的count操作
        long itemCount = itemRepository.count();
        long stockCount = stockRepository.count();
        long movementCount = stockMovementRepository.count();
        long slipCount = slipRepository.count();
        long slipDetailCount = slipDetailRepository.count();
        long storageLocationCount = storageLocationRepository.count();
        long userCount = userRepository.count();
        long warehouseCount = warehouseRepository.count();
        
        assertThat(itemCount).isGreaterThanOrEqualTo(0);
        assertThat(stockCount).isGreaterThanOrEqualTo(0);
        assertThat(movementCount).isGreaterThanOrEqualTo(0);
        assertThat(slipCount).isGreaterThanOrEqualTo(0);
        assertThat(slipDetailCount).isGreaterThanOrEqualTo(0);
        assertThat(storageLocationCount).isGreaterThanOrEqualTo(0);
        assertThat(userCount).isGreaterThanOrEqualTo(0);
        assertThat(warehouseCount).isGreaterThanOrEqualTo(0);
    }
    //endregion
}