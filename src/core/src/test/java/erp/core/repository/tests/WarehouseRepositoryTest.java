package erp.core.repository.tests;

import erp.core.entity.Warehouse;
import erp.core.repository.WarehouseRepository;
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
public class WarehouseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WarehouseRepository warehouseRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setWarehouseRepository(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

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
}