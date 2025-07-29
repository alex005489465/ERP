package erp.core.repository;

import erp.core.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    
    /**
     * 根據倉庫ID查找儲位
     */
    List<StorageLocation> findByWarehouseId(Long warehouseId);
    
    /**
     * 根據儲位編號查找儲位
     */
    Optional<StorageLocation> findByCode(String code);
    
    /**
     * 根據狀態查找儲位
     */
    List<StorageLocation> findByStatus(Byte status);
    
    /**
     * 根據區域查找儲位
     */
    List<StorageLocation> findByZone(String zone);
    
    /**
     * 根據倉庫ID和狀態查找儲位
     */
    List<StorageLocation> findByWarehouseIdAndStatus(Long warehouseId, Byte status);
    
    /**
     * 根據倉庫ID和區域查找儲位
     */
    List<StorageLocation> findByWarehouseIdAndZone(Long warehouseId, String zone);
    
    /**
     * 根據單位查找儲位
     */
    List<StorageLocation> findByUnit(String unit);
    
    /**
     * 檢查儲位編號是否已存在
     */
    boolean existsByCode(String code);
    
    /**
     * 檢查指定倉庫是否有儲位
     */
    boolean existsByWarehouseId(Long warehouseId);
    
    /**
     * 查找容量大於指定值的儲位
     */
    List<StorageLocation> findByCapacityGreaterThan(Integer capacity);
    
    /**
     * 查找承重限制大於指定值的儲位
     */
    List<StorageLocation> findByWeightLimitGreaterThan(BigDecimal weightLimit);
    
    /**
     * 根據倉庫ID和容量範圍查找儲位
     */
    @Query("SELECT sl FROM StorageLocation sl WHERE sl.warehouseId = :warehouseId AND sl.capacity BETWEEN :minCapacity AND :maxCapacity")
    List<StorageLocation> findByWarehouseIdAndCapacityBetween(@Param("warehouseId") Long warehouseId, 
                                                              @Param("minCapacity") Integer minCapacity, 
                                                              @Param("maxCapacity") Integer maxCapacity);
    
    /**
     * 統計指定倉庫的儲位數量
     */
    @Query("SELECT COUNT(sl) FROM StorageLocation sl WHERE sl.warehouseId = :warehouseId")
    Long countByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    /**
     * 統計指定狀態的儲位數量
     */
    @Query("SELECT COUNT(sl) FROM StorageLocation sl WHERE sl.status = :status")
    Long countByStatus(@Param("status") Byte status);
    
    /**
     * 計算指定倉庫的總容量
     */
    @Query("SELECT COALESCE(SUM(sl.capacity), 0) FROM StorageLocation sl WHERE sl.warehouseId = :warehouseId AND sl.status = 1")
    Long getTotalCapacityByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    /**
     * 查找所有不同的區域
     */
    @Query("SELECT DISTINCT sl.zone FROM StorageLocation sl WHERE sl.zone IS NOT NULL ORDER BY sl.zone")
    List<String> findAllDistinctZones();
    
    /**
     * 查找所有不同的單位
     */
    @Query("SELECT DISTINCT sl.unit FROM StorageLocation sl WHERE sl.unit IS NOT NULL ORDER BY sl.unit")
    List<String> findAllDistinctUnits();
    
    /**
     * 根據倉庫ID查找啟用狀態的儲位
     */
    @Query("SELECT sl FROM StorageLocation sl WHERE sl.warehouseId = :warehouseId AND sl.status = 1 ORDER BY sl.code")
    List<StorageLocation> findActiveStorageLocationsByWarehouseId(@Param("warehouseId") Long warehouseId);
}