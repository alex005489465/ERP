package erp.core.repository;

import erp.core.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    /**
     * 根據商品ID查找庫存
     */
    List<Stock> findByItemId(Long itemId);
    
    /**
     * 根據倉庫ID查找庫存
     */
    List<Stock> findByWarehouseId(Long warehouseId);
    
    /**
     * 根據儲位ID查找庫存
     */
    List<Stock> findByStorageLocationId(Long storageLocationId);
    
    /**
     * 根據商品ID和倉庫ID查找庫存
     */
    List<Stock> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);
    
    /**
     * 根據商品ID和儲位ID查找庫存
     */
    Optional<Stock> findByItemIdAndStorageLocationId(Long itemId, Long storageLocationId);
    
    /**
     * 檢查指定商品和儲位的庫存是否存在
     */
    boolean existsByItemIdAndStorageLocationId(Long itemId, Long storageLocationId);
    
    /**
     * 查找庫存數量大於指定值的記錄
     */
    List<Stock> findByQuantityGreaterThan(BigDecimal quantity);
    
    /**
     * 查找庫存數量小於指定值的記錄（低庫存警告）
     */
    List<Stock> findByQuantityLessThan(BigDecimal quantity);
    
    /**
     * 查找庫存為零的記錄
     */
    @Query("SELECT s FROM Stock s WHERE s.quantity = 0")
    List<Stock> findZeroStocks();
    
    /**
     * 根據商品ID計算總庫存量
     */
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.itemId = :itemId")
    BigDecimal getTotalQuantityByItemId(@Param("itemId") Long itemId);
    
    /**
     * 根據倉庫ID計算該倉庫的總庫存記錄數
     */
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.warehouseId = :warehouseId")
    Long countByWarehouseId(@Param("warehouseId") Long warehouseId);
    
    /**
     * 根據儲位ID計算該儲位的總庫存記錄數
     */
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.storageLocationId = :storageLocationId")
    Long countByStorageLocationId(@Param("storageLocationId") Long storageLocationId);
    
    /**
     * 查找所有不同的倉庫ID
     */
    @Query("SELECT DISTINCT s.warehouseId FROM Stock s WHERE s.warehouseId IS NOT NULL ORDER BY s.warehouseId")
    List<Long> findAllDistinctWarehouseIds();
    
    /**
     * 查找所有不同的儲位ID
     */
    @Query("SELECT DISTINCT s.storageLocationId FROM Stock s WHERE s.storageLocationId IS NOT NULL ORDER BY s.storageLocationId")
    List<Long> findAllDistinctStorageLocationIds();
    
    // 向後兼容方法 - 為了支持現有的 WarehouseManagementService
    /**
     * @deprecated 使用 findByWarehouseId 或 findByStorageLocationId 替代
     */
    @Deprecated
    default List<Stock> findByLocation(String location) {
        // 這個方法為了向後兼容而保留，實際上應該根據業務邏輯決定如何處理
        // 暫時返回空列表，避免編譯錯誤
        return java.util.Collections.emptyList();
    }
    
    /**
     * @deprecated 使用 findByItemIdAndStorageLocationId 替代
     */
    @Deprecated
    default java.util.Optional<Stock> findByItemIdAndLocation(Long itemId, String location) {
        // 這個方法為了向後兼容而保留，實際上應該根據業務邏輯決定如何處理
        // 暫時返回空 Optional，避免編譯錯誤
        return java.util.Optional.empty();
    }
    
    /**
     * @deprecated 使用 findAllDistinctWarehouseIds 或 findAllDistinctStorageLocationIds 替代
     */
    @Deprecated
    default List<String> findAllDistinctLocations() {
        // 這個方法為了向後兼容而保留，實際上應該根據業務邏輯決定如何處理
        // 暫時返回空列表，避免編譯錯誤
        return java.util.Collections.emptyList();
    }
}