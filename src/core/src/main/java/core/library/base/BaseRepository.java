package core.library.base;

import core.library.contract.repository.IBaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 基礎資料存取實作類別
 * <p>使用 JPA 和 Specification 實作 IBaseRepository 接口</p>
 * 
 * @param <T> 實體類型
 * @param <ID> 主鍵類型
 */
@Transactional(readOnly = true)
public class BaseRepository<T, ID extends Serializable> implements IBaseRepository<T, ID> {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final SimpleJpaRepository<T, ID> jpaRepository;
    private final Class<T> domainClass;
    
    public BaseRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpaRepository = new SimpleJpaRepository<>(entityInformation, entityManager);
        this.domainClass = entityInformation.getJavaType();
    }
    
    @Override
    public List<T> find() {
        return jpaRepository.findAll();
    }
    
    @Override
    public Optional<T> findFirst(Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            Pageable pageable = PageRequest.of(0, 1);
            List<T> results = jpaRepository.findAll(pageable).getContent();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
        
        Specification<T> spec = buildSpecification(conditions);
        Pageable pageable = PageRequest.of(0, 1);
        List<T> results = jpaRepository.findAll(spec, pageable).getContent();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    @Transactional
    public T save(T entity) {
        return jpaRepository.save(entity);
    }
    
    @Override
    @Transactional
    public boolean delete(T entity) {
        try {
            jpaRepository.delete(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean exists(Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return jpaRepository.count() > 0;
        }
        
        Specification<T> spec = buildSpecification(conditions);
        return jpaRepository.count(spec) > 0;
    }
    
    @Override
    public long count(Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return jpaRepository.count();
        }
        
        Specification<T> spec = buildSpecification(conditions);
        return jpaRepository.count(spec);
    }

    /**
     * 將條件 Map 轉換為 JPA Specification
     * <p>
     * 支援等值查詢，可擴展支援其他查詢條件
     *
     * @param conditions 查詢條件 Map，格式為 Map&lt;String, Object&gt;
     *                   <p>
     *                   - Key: 實體欄位名稱（必須與實體類別的屬性名稱完全一致）<br>
     *                   - Value: 查詢值（null 值會被自動忽略）
     *
     * <p><b>使用範例：</b>
     *
     * <p>Item 實體查詢範例：
     * <pre>{@code
     * Map<String, Object> conditions = new HashMap<>();
     * conditions.put("name", "商品A");           // 查詢名稱等於 "商品A" 的商品
     * conditions.put("unit", "個");             // 查詢單位等於 "個" 的商品
     * conditions.put("id", 1L);                // 查詢 ID 等於 1 的商品
     * }</pre>
     *
     * <p><b>注意事項：</b>
     * <ul>
     * <li>欄位名稱必須與實體類別的屬性名稱完全一致（區分大小寫）</li>
     * <li>null 值會被自動過濾，不會加入查詢條件</li>
     * <li>不存在的欄位名稱會被忽略，不會拋出異常</li>
     * <li>目前僅支援等值查詢（=），未來可擴展支援其他操作符（LIKE、&gt;、&lt;、IN 等）</li>
     * <li>多個條件之間使用 AND 邏輯組合</li>
     * </ul>
     *
     * @return JPA Specification 物件，用於動態查詢
     */
    private Specification<T> buildSpecification(Map<String, Object> conditions) {
        return (root, query, criteriaBuilder) -> {
            if (conditions == null || conditions.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            var predicates = conditions.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    String fieldName = entry.getKey();
                    Object value = entry.getValue();
                    
                    try {
                        // 支援等值查詢
                        return criteriaBuilder.equal(root.get(fieldName), value);
                    } catch (IllegalArgumentException e) {
                        // 如果欄位不存在，忽略此條件
                        return criteriaBuilder.conjunction();
                    }
                })
                .toArray(jakarta.persistence.criteria.Predicate[]::new);
            
            return criteriaBuilder.and(predicates);
        };
    }
}