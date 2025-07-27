package core.domain.entity;

import core.library.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品實體類別
 * 對應資料表: items
 */
@Entity
@Table(name = "items")
public class Item extends BaseEntity {
    
    @Column(name = "name", length = 100, columnDefinition = "VARCHAR(100)")
    private String name;
    
    @Column(name = "unit", length = 20, columnDefinition = "VARCHAR(20)")
    private String unit;
    
    // 預設建構子
    public Item() {
    }
    
    // 帶參數建構子
    public Item(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
    
    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}