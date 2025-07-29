package erp.core.repository;

import erp.core.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    /**
     * 根據倉庫名稱查找倉庫
     */
    Optional<Warehouse> findByName(String name);
    
    /**
     * 根據類型查找倉庫
     */
    List<Warehouse> findByType(String type);
    
    /**
     * 根據狀態查找倉庫
     */
    List<Warehouse> findByStatus(Byte status);
    
    /**
     * 根據地點查找倉庫
     */
    List<Warehouse> findByLocation(String location);
    
    /**
     * 根據類型和狀態查找倉庫
     */
    List<Warehouse> findByTypeAndStatus(String type, Byte status);
    
    /**
     * 根據名稱模糊查詢倉庫
     */
    List<Warehouse> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根據地點模糊查詢倉庫
     */
    List<Warehouse> findByLocationContainingIgnoreCase(String location);
    
    /**
     * 檢查倉庫名稱是否已存在
     */
    boolean existsByName(String name);
    
    /**
     * 檢查指定類型是否有倉庫
     */
    boolean existsByType(String type);
    
    /**
     * 查找面積大於指定值的倉庫
     */
    List<Warehouse> findByAreaM2GreaterThan(BigDecimal areaM2);
    
    /**
     * 查找面積小於指定值的倉庫
     */
    List<Warehouse> findByAreaM2LessThan(BigDecimal areaM2);
    
    /**
     * 根據時間範圍查找建立的倉庫
     */
    List<Warehouse> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 查找啟用狀態的倉庫
     */
    @Query("SELECT w FROM Warehouse w WHERE w.status = 1 ORDER BY w.name")
    List<Warehouse> findActiveWarehouses();
    
    /**
     * 查找停用狀態的倉庫
     */
    @Query("SELECT w FROM Warehouse w WHERE w.status = 0 ORDER BY w.name")
    List<Warehouse> findInactiveWarehouses();
    
    /**
     * 根據面積範圍查找倉庫
     */
    @Query("SELECT w FROM Warehouse w WHERE w.areaM2 BETWEEN :minArea AND :maxArea")
    List<Warehouse> findByAreaM2Between(@Param("minArea") BigDecimal minArea, @Param("maxArea") BigDecimal maxArea);
    
    /**
     * 統計指定類型的倉庫數量
     */
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.type = :type")
    Long countByType(@Param("type") String type);
    
    /**
     * 統計指定狀態的倉庫數量
     */
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.status = :status")
    Long countByStatus(@Param("status") Byte status);
    
    /**
     * 統計啟用狀態的倉庫數量
     */
    @Query("SELECT COUNT(w) FROM Warehouse w WHERE w.status = 1")
    Long countActiveWarehouses();
    
    /**
     * 計算所有啟用倉庫的總面積
     */
    @Query("SELECT COALESCE(SUM(w.areaM2), 0) FROM Warehouse w WHERE w.status = 1")
    BigDecimal getTotalActiveWarehouseArea();
    
    /**
     * 計算指定類型倉庫的總面積
     */
    @Query("SELECT COALESCE(SUM(w.areaM2), 0) FROM Warehouse w WHERE w.type = :type AND w.status = 1")
    BigDecimal getTotalAreaByType(@Param("type") String type);
    
    /**
     * 查找所有不同的倉庫類型
     */
    @Query("SELECT DISTINCT w.type FROM Warehouse w WHERE w.type IS NOT NULL ORDER BY w.type")
    List<String> findAllDistinctTypes();
    
    /**
     * 查找所有不同的地點
     */
    @Query("SELECT DISTINCT w.location FROM Warehouse w WHERE w.location IS NOT NULL ORDER BY w.location")
    List<String> findAllDistinctLocations();
    
    /**
     * 根據類型查找啟用狀態的倉庫
     */
    @Query("SELECT w FROM Warehouse w WHERE w.type = :type AND w.status = 1 ORDER BY w.name")
    List<Warehouse> findActiveWarehousesByType(@Param("type") String type);
    
    /**
     * 查找最近建立的N個倉庫
     */
    List<Warehouse> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根據類型查找最近建立的N個倉庫
     */
    List<Warehouse> findTop10ByTypeOrderByCreatedAtDesc(String type);
}