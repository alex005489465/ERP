package erp.core.repository;

import erp.core.entity.SlipDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlipDetailRepository extends JpaRepository<SlipDetail, Long> {
    
    /**
     * 根據單據ID查找明細
     */
    List<SlipDetail> findBySlipId(Long slipId);
    
    /**
     * 根據單據ID和項次查找明細
     */
    SlipDetail findBySlipIdAndLineNumber(Long slipId, Integer lineNumber);
    
    /**
     * 根據商品ID查找明細
     */
    List<SlipDetail> findByItemId(Long itemId);
    
    /**
     * 根據來源倉庫ID查找明細
     */
    List<SlipDetail> findByFromWarehouseId(Long fromWarehouseId);
    
    /**
     * 根據目標倉庫ID查找明細
     */
    List<SlipDetail> findByToWarehouseId(Long toWarehouseId);
    
    /**
     * 根據來源儲位ID查找明細
     */
    List<SlipDetail> findByFromStorageLocationId(Long fromStorageLocationId);
    
    /**
     * 根據目標儲位ID查找明細
     */
    List<SlipDetail> findByToStorageLocationId(Long toStorageLocationId);
    
    /**
     * 根據單據ID查找明細，按項次排序
     */
    List<SlipDetail> findBySlipIdOrderByLineNumber(Long slipId);
    
    /**
     * 根據單據ID和商品ID查找明細
     */
    List<SlipDetail> findBySlipIdAndItemId(Long slipId, Long itemId);
    
    /**
     * 檢查指定單據是否有明細
     */
    boolean existsBySlipId(Long slipId);
    
    /**
     * 統計指定單據的明細數量
     */
    @Query("SELECT COUNT(sd) FROM SlipDetail sd WHERE sd.slipId = :slipId")
    Long countBySlipId(@Param("slipId") Long slipId);
    
    /**
     * 統計指定商品的明細數量
     */
    @Query("SELECT COUNT(sd) FROM SlipDetail sd WHERE sd.itemId = :itemId")
    Long countByItemId(@Param("itemId") Long itemId);
    
    /**
     * 查找指定單據的最大項次
     */
    @Query("SELECT MAX(sd.lineNumber) FROM SlipDetail sd WHERE sd.slipId = :slipId")
    Integer findMaxLineNumberBySlipId(@Param("slipId") Long slipId);
    
    /**
     * 根據倉庫ID查找相關明細（包含來源和目標倉庫）
     */
    @Query("SELECT sd FROM SlipDetail sd WHERE sd.fromWarehouseId = :warehouseId OR sd.toWarehouseId = :warehouseId")
    List<SlipDetail> findByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    /**
     * 根據儲位ID查找相關明細（包含來源和目標儲位）
     */
    @Query("SELECT sd FROM SlipDetail sd WHERE sd.fromStorageLocationId = :storageLocationId OR sd.toStorageLocationId = :storageLocationId")
    List<SlipDetail> findByStorageLocationId(@Param("storageLocationId") Long storageLocationId);
    
    /**
     * 根據狀態查找明細
     */
    List<SlipDetail> findByStatus(SlipDetail.Status status);
    
    /**
     * 根據單據ID和狀態查找明細（利用複合索引 idx_slip_details_slip_status）
     */
    List<SlipDetail> findBySlipIdAndStatus(Long slipId, SlipDetail.Status status);
    
    /**
     * 根據單據ID和狀態查找明細，按項次排序
     */
    List<SlipDetail> findBySlipIdAndStatusOrderByLineNumber(Long slipId, SlipDetail.Status status);
    
    /**
     * 統計指定單據指定狀態的明細數量
     */
    @Query("SELECT COUNT(sd) FROM SlipDetail sd WHERE sd.slipId = :slipId AND sd.status = :status")
    Long countBySlipIdAndStatus(@Param("slipId") Long slipId, @Param("status") SlipDetail.Status status);
    
    /**
     * 檢查指定單據是否有指定狀態的明細
     */
    boolean existsBySlipIdAndStatus(Long slipId, SlipDetail.Status status);
}