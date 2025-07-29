package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "slips",
       indexes = {
           @Index(name = "idx_slips_type", columnList = "slips_type"),
           @Index(name = "idx_created_by", columnList = "created_by"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Slip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '單據唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "slips_type", nullable = true, columnDefinition = "TINYINT NULL COMMENT '單據類型：1=入庫單, 2=出庫單, 3=轉倉單, 4=報廢單'")
    private Byte slipsType;
    
    @Column(name = "created_by", nullable = true, columnDefinition = "BIGINT NULL COMMENT '建立人（users.id）'")
    private Long createdBy;
    
    @Column(nullable = true, columnDefinition = "TINYINT NULL COMMENT '狀態：0=草稿, 1=完成, 2=取消'")
    private Byte status;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}