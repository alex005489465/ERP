package erp.core.repository;

import erp.core.entity.SlipMovement;
import erp.core.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlipMovementRepository extends JpaRepository<SlipMovement, Long> {
    
    /**
     * 根據單據ID查找關聯記錄
     */
    List<SlipMovement> findBySlipId(Long slipId);
    
    /**
     * 根據庫存異動ID查找關聯記錄
     */
    List<SlipMovement> findByStockMovementId(Long stockMovementId);
    
    /**
     * 根據單據ID查找所有關聯的庫存異動記錄
     * 這個方法替代了原來 StockMovementRepository.findBySlipId 的功能
     */
    @Query("SELECT sm.stockMovement FROM SlipMovement sm WHERE sm.slipId = :slipId")
    List<StockMovement> findStockMovementsBySlipId(@Param("slipId") Long slipId);
    
    /**
     * 根據庫存異動ID查找所有關聯的單據ID
     */
    @Query("SELECT sm.slipId FROM SlipMovement sm WHERE sm.stockMovementId = :stockMovementId")
    List<Long> findSlipIdsByStockMovementId(@Param("stockMovementId") Long stockMovementId);
    
    /**
     * 檢查指定單據和庫存異動是否已經關聯
     */
    boolean existsBySlipIdAndStockMovementId(Long slipId, Long stockMovementId);
    
    /**
     * 根據單據ID刪除所有關聯記錄
     */
    void deleteBySlipId(Long slipId);
    
    /**
     * 根據庫存異動ID刪除所有關聯記錄
     */
    void deleteByStockMovementId(Long stockMovementId);
    
    /**
     * 統計指定單據的關聯庫存異動數量
     */
    @Query("SELECT COUNT(sm) FROM SlipMovement sm WHERE sm.slipId = :slipId")
    Long countBySlipId(@Param("slipId") Long slipId);
    
    /**
     * 統計指定庫存異動的關聯單據數量
     */
    @Query("SELECT COUNT(sm) FROM SlipMovement sm WHERE sm.stockMovementId = :stockMovementId")
    Long countByStockMovementId(@Param("stockMovementId") Long stockMovementId);
}