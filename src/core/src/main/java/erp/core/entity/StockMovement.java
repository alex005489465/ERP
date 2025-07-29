package erp.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements",
       indexes = {
           @Index(name = "idx_stock_movements_item_created", columnList = "item_id, created_at"),
           @Index(name = "idx_stock_movements_created_at", columnList = "created_at"),
           @Index(name = "idx_stock_movements_warehouse_id", columnList = "warehouse_id"),
           @Index(name = "idx_stock_movements_storage_location_id", columnList = "storage_location_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    
    /**
     * 庫存異動類型枚舉
     */
    @Getter
    public enum MovementType {
        INBOUND(1, "入庫"),
        OUTBOUND(2, "出庫");
        
        private final int code;
        private final String description;
        
        MovementType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根據代碼獲取枚舉值
         */
        public static MovementType fromCode(int code) {
            for (MovementType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown movement type code: " + code);
        }
    }
    
    /**
     * MovementType 與 Integer 之間的轉換器
     */
    @Converter
    public static class MovementTypeConverter implements AttributeConverter<MovementType, Integer> {
        
        @Override
        public Integer convertToDatabaseColumn(MovementType attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getCode();
        }
        
        @Override
        public MovementType convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return null;
            }
            return MovementType.fromCode(dbData);
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '庫存異動記錄唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "item_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '商品ID'")
    private Long itemId;
    
    @Column(name = "warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '倉庫ID'")
    private Long warehouseId;
    
    @Column(name = "storage_location_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '儲位ID'")
    private Long storageLocationId;
    
    @Column(nullable = true, columnDefinition = "INT NULL COMMENT '異動類型（數字代碼，如入庫=1，出庫=2）'")
    @Convert(converter = MovementTypeConverter.class)
    private MovementType type; // 入庫=1，出庫=2
    
    @Column(name = "quantity_change", precision = 18, scale = 6, nullable = true, columnDefinition = "DECIMAL(18,6) NULL COMMENT '異動數量'")
    private BigDecimal quantityChange;
    
    @Column(columnDefinition = "TEXT NULL COMMENT '異動備註'", nullable = true)
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '異動發生時間（毫秒級）'")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, columnDefinition = "DATETIME(3) NULL COMMENT '更新時間（毫秒級）'")
    private LocalDateTime updatedAt;
}