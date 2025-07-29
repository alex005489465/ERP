package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses",
       indexes = {
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_type", columnList = "type")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '倉庫唯一識別碼，自增'")
    private Long id;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '倉庫名稱'")
    private String name;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '類型（常溫/冷藏/GMP等）'")
    private String type;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '地點/地址'")
    private String location;
    
    @Column(name = "area_m2", precision = 10, scale = 2, nullable = true, columnDefinition = "DECIMAL(10,2) NULL COMMENT '總面積（單位：平方公尺）'")
    private BigDecimal areaM2;
    
    @Column(nullable = true, columnDefinition = "TINYINT NULL COMMENT '狀態：0=停用, 1=啟用'")
    private Byte status;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}