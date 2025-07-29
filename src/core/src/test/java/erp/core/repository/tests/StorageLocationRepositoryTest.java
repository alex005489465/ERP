package erp.core.repository.tests;

import erp.core.entity.StorageLocation;
import erp.core.repository.StorageLocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class StorageLocationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setStorageLocationRepository(StorageLocationRepository storageLocationRepository) {
        this.storageLocationRepository = storageLocationRepository;
    }

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
}