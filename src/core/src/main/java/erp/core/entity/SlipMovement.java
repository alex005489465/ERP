package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "slip_movements",
       indexes = {
           @Index(name = "idx_slip_movements_slip_id", columnList = "slip_id"),
           @Index(name = "idx_slip_movements_stock_movement_id", columnList = "stock_movement_id")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_slip_movements_slip_stock", columnNames = {"slip_id", "stock_movement_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlipMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '關聯記錄唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "slip_id", nullable = false, columnDefinition = "BIGINT NOT NULL COMMENT '單據ID（關聯到 slips.id）'")
    private Long slipId;
    
    @Column(name = "stock_movement_id", nullable = false, columnDefinition = "BIGINT NOT NULL COMMENT '庫存異動ID（關聯到 stock_movements.id）'")
    private Long stockMovementId;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '建立時間（毫秒級）'")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
    
    // JPA relationships for easier querying
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slip_id", insertable = false, updatable = false)
    private Slip slip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_movement_id", insertable = false, updatable = false)
    private StockMovement stockMovement;
}