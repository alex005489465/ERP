package erp.core.repository;

import erp.core.entity.StockMovement;
import erp.core.entity.StockMovement.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    /**
     * 根據商品ID查找庫存異動記錄
     */
    List<StockMovement> findByItemId(Long itemId);
    
    /**
     * 根據倉庫ID查找庫存異動記錄
     */
    List<StockMovement> findByWarehouseId(Long warehouseId);
    
    /**
     * 根據儲位ID查找庫存異動記錄
     */
    List<StockMovement> findByStorageLocationId(Long storageLocationId);
    
    /**
     * 根據單據ID查找庫存異動記錄
     */
    List<StockMovement> findBySlipId(Long slipId);
    
    /**
     * 根據異動類型查找記錄 (INBOUND=入庫, OUTBOUND=出庫)
     */
    List<StockMovement> findByType(MovementType type);
    
    /**
     * 根據商品ID和倉庫ID查找庫存異動記錄
     */
    List<StockMovement> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);
    
    /**
     * 根據商品ID和儲位ID查找庫存異動記錄
     */
    List<StockMovement> findByItemIdAndStorageLocationId(Long itemId, Long storageLocationId);
    
    /**
     * 根據商品ID和異動類型查找記錄
     */
    List<StockMovement> findByItemIdAndType(Long itemId, MovementType type);
    
    /**
     * 根據時間範圍查找庫存異動記錄
     */
    List<StockMovement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根據商品ID和時間範圍查找記錄
     */
    List<StockMovement> findByItemIdAndCreatedAtBetween(Long itemId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 查找指定商品的入庫記錄
     */
    @Query("SELECT sm FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.type = :inboundType ORDER BY sm.createdAt DESC")
    List<StockMovement> findInboundMovementsByItemId(@Param("itemId") Long itemId, @Param("inboundType") MovementType inboundType);
    
    /**
     * 查找指定商品的出庫記錄
     */
    @Query("SELECT sm FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.type = :outboundType ORDER BY sm.createdAt DESC")
    List<StockMovement> findOutboundMovementsByItemId(@Param("itemId") Long itemId, @Param("outboundType") MovementType outboundType);
    
    /**
     * 計算指定商品的總入庫量
     */
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.type = :inboundType")
    BigDecimal getTotalInboundQuantityByItemId(@Param("itemId") Long itemId, @Param("inboundType") MovementType inboundType);
    
    /**
     * 計算指定商品的總出庫量
     */
    @Query("SELECT COALESCE(SUM(sm.quantityChange), 0) FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.type = :outboundType")
    BigDecimal getTotalOutboundQuantityByItemId(@Param("itemId") Long itemId, @Param("outboundType") MovementType outboundType);
    
    /**
     * 根據商品ID和倉庫ID計算淨異動量
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN sm.type = :inboundType THEN sm.quantityChange ELSE -sm.quantityChange END), 0) " +
           "FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.warehouseId = :warehouseId")
    BigDecimal getNetMovementByItemIdAndWarehouseId(@Param("itemId") Long itemId, @Param("warehouseId") Long warehouseId, @Param("inboundType") MovementType inboundType);
    
    /**
     * 根據商品ID和儲位ID計算淨異動量
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN sm.type = :inboundType THEN sm.quantityChange ELSE -sm.quantityChange END), 0) " +
           "FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.storageLocationId = :storageLocationId")
    BigDecimal getNetMovementByItemIdAndStorageLocationId(@Param("itemId") Long itemId, @Param("storageLocationId") Long storageLocationId, @Param("inboundType") MovementType inboundType);
    
    /**
     * 查找最近的N筆異動記錄
     */
    List<StockMovement> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根據商品ID查找最近的N筆異動記錄
     */
    List<StockMovement> findTop10ByItemIdOrderByCreatedAtDesc(Long itemId);
    
    /**
     * 查找所有不同的倉庫ID
     */
    @Query("SELECT DISTINCT sm.warehouseId FROM StockMovement sm WHERE sm.warehouseId IS NOT NULL ORDER BY sm.warehouseId")
    List<Long> findAllDistinctWarehouseIds();
    
    /**
     * 查找所有不同的儲位ID
     */
    @Query("SELECT DISTINCT sm.storageLocationId FROM StockMovement sm WHERE sm.storageLocationId IS NOT NULL ORDER BY sm.storageLocationId")
    List<Long> findAllDistinctStorageLocationIds();
    
    // 向後兼容方法 - 為了支持現有的 WarehouseManagementService
    /**
     * @deprecated 使用 findByWarehouseId 或 findByStorageLocationId 替代
     */
    @Deprecated
    default List<StockMovement> findByLocation(String location) {
        // 這個方法為了向後兼容而保留，實際上應該根據業務邏輯決定如何處理
        // 暫時返回空列表，避免編譯錯誤
        return java.util.Collections.emptyList();
    }
}