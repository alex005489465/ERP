package erp.core.repository;

import erp.core.entity.Slip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlipRepository extends JpaRepository<Slip, Long> {
    
    /**
     * 根據單據類型查找單據
     */
    List<Slip> findBySlipsType(Byte slipsType);
    
    /**
     * 根據建立人查找單據
     */
    List<Slip> findByCreatedBy(Long createdBy);
    
    /**
     * 根據狀態查找單據
     */
    List<Slip> findByStatus(Byte status);
    
    /**
     * 根據單據類型和狀態查找單據
     */
    List<Slip> findBySlipsTypeAndStatus(Byte slipsType, Byte status);
    
    /**
     * 根據建立人和狀態查找單據
     */
    List<Slip> findByCreatedByAndStatus(Long createdBy, Byte status);
    
    /**
     * 根據時間範圍查找單據
     */
    List<Slip> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根據建立人和時間範圍查找單據
     */
    List<Slip> findByCreatedByAndCreatedAtBetween(Long createdBy, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 檢查指定建立人是否有未完成的單據
     */
    boolean existsByCreatedByAndStatusNot(Long createdBy, Byte completedStatus);
    
    /**
     * 查找指定類型的最新N筆單據
     */
    List<Slip> findTop10BySlipsTypeOrderByCreatedAtDesc(Byte slipsType);
    
    /**
     * 查找指定建立人的最新N筆單據
     */
    List<Slip> findTop10ByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    /**
     * 統計指定類型和狀態的單據數量
     */
    @Query("SELECT COUNT(s) FROM Slip s WHERE s.slipsType = :slipsType AND s.status = :status")
    Long countBySlipsTypeAndStatus(@Param("slipsType") Byte slipsType, @Param("status") Byte status);
    
    /**
     * 統計指定建立人的單據數量
     */
    @Query("SELECT COUNT(s) FROM Slip s WHERE s.createdBy = :createdBy")
    Long countByCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * 查找所有不同的單據類型
     */
    @Query("SELECT DISTINCT s.slipsType FROM Slip s WHERE s.slipsType IS NOT NULL ORDER BY s.slipsType")
    List<Byte> findAllDistinctSlipsTypes();
}