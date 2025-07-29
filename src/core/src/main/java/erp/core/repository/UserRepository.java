package erp.core.repository;

import erp.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根據姓名查找使用者
     */
    Optional<User> findByName(String name);
    
    /**
     * 根據角色查找使用者
     */
    List<User> findByRole(String role);
    
    /**
     * 根據狀態查找使用者
     */
    List<User> findByStatus(Byte status);
    
    /**
     * 根據角色和狀態查找使用者
     */
    List<User> findByRoleAndStatus(String role, Byte status);
    
    /**
     * 根據姓名模糊查詢使用者
     */
    List<User> findByNameContainingIgnoreCase(String name);
    
    /**
     * 檢查姓名是否已存在
     */
    boolean existsByName(String name);
    
    /**
     * 檢查指定角色是否有使用者
     */
    boolean existsByRole(String role);
    
    /**
     * 根據時間範圍查找建立的使用者
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 查找啟用狀態的使用者
     */
    @Query("SELECT u FROM User u WHERE u.status = 1 ORDER BY u.name")
    List<User> findActiveUsers();
    
    /**
     * 查找停用狀態的使用者
     */
    @Query("SELECT u FROM User u WHERE u.status = 0 ORDER BY u.name")
    List<User> findInactiveUsers();
    
    /**
     * 統計指定角色的使用者數量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
    
    /**
     * 統計指定狀態的使用者數量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countByStatus(@Param("status") Byte status);
    
    /**
     * 統計啟用狀態的使用者數量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    Long countActiveUsers();
    
    /**
     * 查找所有不同的角色
     */
    @Query("SELECT DISTINCT u.role FROM User u WHERE u.role IS NOT NULL ORDER BY u.role")
    List<String> findAllDistinctRoles();
    
    /**
     * 根據角色查找啟用狀態的使用者
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 1 ORDER BY u.name")
    List<User> findActiveUsersByRole(@Param("role") String role);
    
    /**
     * 查找最近建立的N個使用者
     */
    List<User> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根據角色查找最近建立的N個使用者
     */
    List<User> findTop10ByRoleOrderByCreatedAtDesc(String role);
}