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
     * 根據位置查找庫存異動記錄
     */
    List<StockMovement> findByLocation(String location);
    
    /**
     * 根據異動類型查找記錄 (INBOUND=入庫, OUTBOUND=出庫)
     */
    List<StockMovement> findByType(MovementType type);
    
    /**
     * 根據商品ID和位置查找庫存異動記錄
     */
    List<StockMovement> findByItemIdAndLocation(Long itemId, String location);
    
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
     * 根據商品ID和位置計算淨異動量
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN sm.type = :inboundType THEN sm.quantityChange ELSE -sm.quantityChange END), 0) " +
           "FROM StockMovement sm WHERE sm.itemId = :itemId AND sm.location = :location")
    BigDecimal getNetMovementByItemIdAndLocation(@Param("itemId") Long itemId, @Param("location") String location, @Param("inboundType") MovementType inboundType);
    
    /**
     * 查找最近的N筆異動記錄
     */
    List<StockMovement> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根據商品ID查找最近的N筆異動記錄
     */
    List<StockMovement> findTop10ByItemIdOrderByCreatedAtDesc(Long itemId);
    
    /**
     * 查找所有不同的異動位置
     */
    @Query("SELECT DISTINCT sm.location FROM StockMovement sm ORDER BY sm.location")
    List<String> findAllDistinctLocations();
}