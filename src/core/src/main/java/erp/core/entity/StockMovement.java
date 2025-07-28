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
@Table(name = "stock_movements",
       indexes = {
           @Index(name = "idx_item_location_time", columnList = "item_id, created_at"),
           @Index(name = "idx_created_at", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    
    /**
     * 庫存異動類型枚舉
     */
    public enum MovementType {
        INBOUND(1, "入庫"),
        OUTBOUND(2, "出庫");
        
        private final int code;
        private final String description;
        
        MovementType(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
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
    
    @Column(length = 50, nullable = true)
    private String location;
    
    @Column(nullable = true)
    @Convert(converter = MovementTypeConverter.class)
    private MovementType type; // 入庫=1，出庫=2
    
    @Column(name = "quantity_change", precision = 18, scale = 6, nullable = true)
    private BigDecimal quantityChange;
    
    @Column(columnDefinition = "TEXT", nullable = true)
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
}