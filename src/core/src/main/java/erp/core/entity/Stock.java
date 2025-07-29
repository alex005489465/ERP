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
@Table(name = "stocks",
       indexes = {
           @Index(name = "idx_stocks_item_id", columnList = "item_id"),
           @Index(name = "idx_stocks_warehouse_id", columnList = "warehouse_id"),
           @Index(name = "idx_stocks_storage_location_id", columnList = "storage_location_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存記錄唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "item_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '對應商品ID'")
    private Long itemId;
    
    @Column(name = "warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '倉庫ID'")
    private Long warehouseId;
    
    @Column(name = "storage_location_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '儲位ID'")
    private Long storageLocationId;
    
    @Column(precision = 18, scale = 6, nullable = true, columnDefinition = "DECIMAL(18,6) NULL COMMENT '現有庫存量'")
    private BigDecimal quantity;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}