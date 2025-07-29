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
@Table(name = "slip_details",
       indexes = {
           @Index(name = "idx_slip_details_slip_id", columnList = "slip_id"),
           @Index(name = "idx_slip_details_slip_status", columnList = "slip_id, status"),
           @Index(name = "idx_slip_details_item_id", columnList = "item_id"),
           @Index(name = "idx_slip_details_from_warehouse_id", columnList = "from_warehouse_id"),
           @Index(name = "idx_slip_details_from_storage_location_id", columnList = "from_storage_location_id"),
           @Index(name = "idx_slip_details_to_warehouse_id", columnList = "to_warehouse_id"),
           @Index(name = "idx_slip_details_to_storage_location_id", columnList = "to_storage_location_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlipDetail {
    
    /**
     * 單據明細狀態枚舉
     */
    @Getter
    public enum Status {
        PENDING(0, "待處理"),
        PROCESSED(1, "已處理"),
        CANCELLED(2, "取消"),
        FAILED(3, "失敗"),;
        
        private final int code;
        private final String description;
        
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 根據代碼獲取枚舉值
         */
        public static Status fromCode(int code) {
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
    }
    
    /**
     * Status 與 Integer 之間的轉換器
     */
    @Converter
    public static class StatusConverter implements AttributeConverter<Status, Integer> {
        
        @Override
        public Integer convertToDatabaseColumn(Status attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.getCode();
        }
        
        @Override
        public Status convertToEntityAttribute(Integer dbData) {
            if (dbData == null) {
                return null;
            }
            return Status.fromCode(dbData);
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明細唯一識別碼，自增'")
    private Long id;
    
    @Column(name = "line_number", nullable = false, columnDefinition = "INTEGER NOT NULL COMMENT '項次（在單據中的順序）'")
    private Integer lineNumber;
    
    @Column(name = "slip_id", nullable = false, columnDefinition = "BIGINT NOT NULL COMMENT '單據ID（關聯到 slips.id）'")
    private Long slipId;
    
    @Column(name = "item_id", nullable = false, columnDefinition = "BIGINT NOT NULL COMMENT '商品ID（關聯到 items.id）'")
    private Long itemId;
    
    @Column(name = "from_warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '來源倉庫ID（關聯到 warehouses.id）'")
    private Long fromWarehouseId;
    
    @Column(name = "from_storage_location_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '來源儲位ID（關聯到 storage_locations.id）'")
    private Long fromStorageLocationId;
    
    @Column(name = "to_warehouse_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '目標倉庫ID（關聯到 warehouses.id）'")
    private Long toWarehouseId;
    
    @Column(name = "to_storage_location_id", nullable = true, columnDefinition = "BIGINT NULL COMMENT '目標儲位ID（關聯到 storage_locations.id）'")
    private Long toStorageLocationId;
    
    @Column(name = "quantity_change", precision = 18, scale = 6, nullable = false, columnDefinition = "DECIMAL(18,6) NOT NULL COMMENT '異動數量'")
    private BigDecimal quantityChange;
    
    @Column(nullable = true, columnDefinition = "TINYINT NULL COMMENT '狀態：0=待處理, 1=已處理, 2=取消'")
    @Convert(converter = StatusConverter.class)
    private Status status;
    
    @Column(columnDefinition = "TEXT NULL COMMENT '異動備註'", nullable = true)
    private String note;
    
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
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id", insertable = false, updatable = false)
    private Warehouse fromWarehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_storage_location_id", insertable = false, updatable = false)
    private StorageLocation fromStorageLocation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id", insertable = false, updatable = false)
    private Warehouse toWarehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_storage_location_id", insertable = false, updatable = false)
    private StorageLocation toStorageLocation;
}