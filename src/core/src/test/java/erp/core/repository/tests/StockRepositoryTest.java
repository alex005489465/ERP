package erp.core.repository.tests;

import erp.core.entity.Stock;
import erp.core.repository.StockRepository;
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
public class StockRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockRepository stockRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setStockRepository(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

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
}