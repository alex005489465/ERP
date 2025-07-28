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
     * 根據位置查找庫存
     */
    List<Stock> findByLocation(String location);
    
    /**
     * 根據商品ID和位置查找庫存
     */
    Optional<Stock> findByItemIdAndLocation(Long itemId, String location);
    
    /**
     * 檢查指定商品和位置的庫存是否存在
     */
    boolean existsByItemIdAndLocation(Long itemId, String location);
    
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
     * 根據位置計算該位置的總庫存記錄數
     */
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.location = :location")
    Long countByLocation(@Param("location") String location);
    
    /**
     * 查找所有不同的庫存位置
     */
    @Query("SELECT DISTINCT s.location FROM Stock s ORDER BY s.location")
    List<String> findAllDistinctLocations();
}