package erp.core.repository;

import erp.core.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * 根據名稱查找商品
     */
    Optional<Item> findByName(String name);
    
    /**
     * 根據名稱模糊查詢商品
     */
    List<Item> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根據單位查找商品
     */
    List<Item> findByUnit(String unit);
    
    /**
     * 檢查商品名稱是否已存在
     */
    boolean existsByName(String name);
    
    /**
     * 根據名稱和單位查找商品
     */
    @Query("SELECT i FROM Item i WHERE i.name = :name AND i.unit = :unit")
    Optional<Item> findByNameAndUnit(@Param("name") String name, @Param("unit") String unit);
}