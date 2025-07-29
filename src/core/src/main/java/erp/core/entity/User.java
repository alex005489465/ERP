package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
       indexes = {
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_role", columnList = "role")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人員唯一識別碼，自增'")
    private Long id;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '姓名'")
    private String name;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '角色（倉管、管理員等）'")
    private String role;
    
    @Column(nullable = true, columnDefinition = "TINYINT NULL COMMENT '狀態：0=停用, 1=啟用, 2=停職'")
    private Byte status;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}