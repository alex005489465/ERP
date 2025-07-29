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
           @Index(name = "idx_item_location_time", columnList = "item_id, created_at"),
           @Index(name = "idx_created_at", columnList = "created_at"),
           @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
           @Index(name = "idx_storage_location_id", columnList = "storage_location_id"),
           @Index(name = "idx_slip_id", columnList = "slip_id")
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
    private Long id;
    
    @Column(name = "item_id", nullable = true)
    private Long itemId;
    
    @Column(name = "warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '倉庫ID'")
    private Long warehouseId;
    
    @Column(name = "storage_location_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '儲位ID'")
    private Long storageLocationId;
    
    @Column(nullable = true)
    @Convert(converter = MovementTypeConverter.class)
    private MovementType type; // 入庫=1，出庫=2
    
    @Column(name = "quantity_change", precision = 18, scale = 6, nullable = true)
    private BigDecimal quantityChange;
    
    @Column(name = "slip_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '單據ID'")
    private Long slipId;
    
    @Column(columnDefinition = "TEXT", nullable = true)
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
}