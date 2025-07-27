package core.domain.entity;

import core.library.base.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 庫存異動紀錄實體類別
 * 對應資料表: stock_movements
 */
@Entity
@Table(name = "stock_movements", indexes = {
    @Index(name = "idx_item_location_time", columnList = "item_id, created_at"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class StockMovement extends BaseEntity {
    
    @Column(name = "item_id", columnDefinition = "BIGINT")
    private Long itemId;
    
    @Column(name = "location", length = 50, columnDefinition = "VARCHAR(50)")
    private String location;
    
    @Column(name = "type", columnDefinition = "INT")
    private Integer type;
    
    @Column(name = "quantity_change", precision = 18, scale = 6, columnDefinition = "DECIMAL(18,6)")
    private BigDecimal quantityChange;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    // 預設建構子
    public StockMovement() {
    }
    
    // 帶參數建構子
    public StockMovement(Long itemId, String location, Integer type, BigDecimal quantityChange) {
        this.itemId = itemId;
        this.location = location;
        this.type = type;
        this.quantityChange = quantityChange;
    }
    
    // 帶完整參數建構子
    public StockMovement(Long itemId, String location, Integer type, BigDecimal quantityChange, String note) {
        this.itemId = itemId;
        this.location = location;
        this.type = type;
        this.quantityChange = quantityChange;
        this.note = note;
    }
    
    // Getter 和 Setter 方法
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public BigDecimal getQuantityChange() {
        return quantityChange;
    }
    
    public void setQuantityChange(BigDecimal quantityChange) {
        this.quantityChange = quantityChange;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", quantityChange=" + quantityChange +
                ", note='" + note + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}