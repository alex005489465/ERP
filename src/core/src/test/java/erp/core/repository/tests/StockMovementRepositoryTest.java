package erp.core.repository.tests;

import erp.core.entity.SlipMovement;
import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import erp.core.repository.SlipMovementRepository;
import erp.core.repository.StockMovementRepository;
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
public class StockMovementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private SlipMovementRepository slipMovementRepository;

    // 用於手動依賴注入的setter方法
    public void setEntityManager(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setStockMovementRepository(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public void setSlipMovementRepository(SlipMovementRepository slipMovementRepository) {
        this.slipMovementRepository = slipMovementRepository;
    }

    @Test
    public void testStockMovementRepository() {
        // 創建測試數據
        StockMovement movement = new StockMovement();
        movement.setItemId(1L);
        movement.setWarehouseId(1L);
        movement.setStorageLocationId(1L);
        movement.setType(MovementType.INBOUND); // 入庫
        movement.setQuantityChange(new BigDecimal("50.00"));
        movement.setNote("測試入庫");
        
        // 保存並刷新
        StockMovement savedMovement = stockMovementRepository.save(movement);
        entityManager.flush();
        
        // 創建單據與庫存異動的關聯記錄
        SlipMovement slipMovement = new SlipMovement();
        slipMovement.setSlipId(1L);
        slipMovement.setStockMovementId(savedMovement.getId());
        slipMovementRepository.save(slipMovement);
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
        
        // 測試按單據ID查找（通過新的關聯表）
        List<StockMovement> movementsBySlipId = slipMovementRepository.findStockMovementsBySlipId(1L);
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
}