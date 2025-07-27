package core.domain.entity;

import core.library.base.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 庫存實體類別
 * 對應資料表: stocks
 */
@Entity
@Table(name = "stocks", indexes = {
    @Index(name = "idx_item_id", columnList = "item_id"),
    @Index(name = "idx_location", columnList = "location")
})
public class Stock extends BaseEntity {
    
    @Column(name = "item_id", columnDefinition = "BIGINT")
    private Long itemId;

    @Column(name = "location", length = 50, columnDefinition = "VARCHAR(50)")
    private String location;
    
    @Column(name = "quantity", precision = 18, scale = 6, columnDefinition = "DECIMAL(18,6)")
    private BigDecimal quantity;
    
    // 預設建構子
    public Stock() {
    }
    
    // 帶參數建構子
    public Stock(Long itemId, String location, BigDecimal quantity) {
        this.itemId = itemId;
        this.location = location;
        this.quantity = quantity;
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
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", location='" + location + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}