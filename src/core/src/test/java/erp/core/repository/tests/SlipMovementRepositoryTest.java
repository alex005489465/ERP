package erp.core.repository.tests;

import erp.core.entity.SlipMovement;
import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import erp.core.repository.SlipMovementRepository;
import erp.core.repository.StockMovementRepository;
import lombok.Setter;
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

@Setter
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class SlipMovementRepositoryTest {

    // 用於手動依賴注入的setter方法
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SlipMovementRepository slipMovementRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Test
    public void testSlipMovementRepository() {
        // 創建測試數據 - 先創建 StockMovement
        StockMovement movement = new StockMovement();
        movement.setItemId(1L);
        movement.setWarehouseId(1L);
        movement.setStorageLocationId(1L);
        movement.setType(MovementType.INBOUND);
        movement.setQuantityChange(new BigDecimal("100.00"));
        movement.setNote("測試庫存異動");
        
        StockMovement savedMovement = stockMovementRepository.save(movement);
        entityManager.flush();
        
        // 創建 SlipMovement 關聯記錄
        SlipMovement slipMovement = new SlipMovement();
        slipMovement.setSlipId(1L);
        slipMovement.setStockMovementId(savedMovement.getId());
        
        SlipMovement savedSlipMovement = slipMovementRepository.save(slipMovement);
        entityManager.flush();
        
        // 測試基本查詢
        Optional<SlipMovement> foundSlipMovement = slipMovementRepository.findById(savedSlipMovement.getId());
        assertThat(foundSlipMovement).isPresent();
        assertThat(foundSlipMovement.get().getSlipId()).isEqualTo(1L);
        assertThat(foundSlipMovement.get().getStockMovementId()).isEqualTo(savedMovement.getId());
        
        // 測試按單據ID查找關聯記錄
        List<SlipMovement> slipMovementsBySlipId = slipMovementRepository.findBySlipId(1L);
        assertThat(slipMovementsBySlipId).hasSize(1);
        
        // 測試按庫存異動ID查找關聯記錄
        List<SlipMovement> slipMovementsByStockMovementId = slipMovementRepository.findByStockMovementId(savedMovement.getId());
        assertThat(slipMovementsByStockMovementId).hasSize(1);
        
        // 測試根據單據ID查找庫存異動記錄
        List<StockMovement> stockMovementsBySlipId = slipMovementRepository.findStockMovementsBySlipId(1L);
        assertThat(stockMovementsBySlipId).hasSize(1);
        assertThat(stockMovementsBySlipId.get(0).getId()).isEqualTo(savedMovement.getId());
        
        // 測試根據庫存異動ID查找單據ID
        List<Long> slipIdsByStockMovementId = slipMovementRepository.findSlipIdsByStockMovementId(savedMovement.getId());
        assertThat(slipIdsByStockMovementId).hasSize(1);
        assertThat(slipIdsByStockMovementId.get(0)).isEqualTo(1L);
        
        // 測試關聯存在性檢查
        boolean exists = slipMovementRepository.existsBySlipIdAndStockMovementId(1L, savedMovement.getId());
        assertThat(exists).isTrue();
        
        // 測試統計功能
        Long countBySlipId = slipMovementRepository.countBySlipId(1L);
        assertThat(countBySlipId).isEqualTo(1L);
        
        Long countByStockMovementId = slipMovementRepository.countByStockMovementId(savedMovement.getId());
        assertThat(countByStockMovementId).isEqualTo(1L);
    }
}