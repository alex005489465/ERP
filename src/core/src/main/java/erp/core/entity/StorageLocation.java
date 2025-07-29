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
@Table(name = "storage_locations",
       indexes = {
           @Index(name = "idx_storage_locations_warehouse_id", columnList = "warehouse_id"),
           @Index(name = "idx_storage_locations_code", columnList = "code"),
           @Index(name = "idx_storage_locations_status", columnList = "status")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '儲位唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '所屬倉庫ID'")
    private Long warehouseId;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '儲位編號（如 A01-B02）'")
    private String code;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '區域/分區'")
    private String zone;
    
    @Column(nullable = true, columnDefinition = "INTEGER NULL COMMENT '可容納容量（數量或件數）'")
    private Integer capacity;
    
    @Column(length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '單位（箱、件、kg）'")
    private String unit;
    
    @Column(name = "size_limit", length = 200, nullable = true, columnDefinition = "VARCHAR(200) NULL COMMENT '尺寸限制（[長,寬,高]，單位 mm）'")
    private String sizeLimit;
    
    @Column(name = "weight_limit", precision = 10, scale = 2, nullable = true, columnDefinition = "DECIMAL(10,2) NULL COMMENT '承重限制（單位：kg）'")
    private BigDecimal weightLimit;
    
    @Column(nullable = true, columnDefinition = "TINYINT NULL COMMENT '狀態：0=停用, 1=啟用, 2=維護中'")
    private Byte status;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}