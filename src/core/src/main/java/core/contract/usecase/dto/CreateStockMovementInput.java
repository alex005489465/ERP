package core.contract.usecase.dto;

import java.math.BigDecimal;

/**
 * 創建庫存異動的輸入參數 DTO
 */
public class CreateStockMovementInput {
    
    /**
     * 商品 ID
     */
    private Long itemId;
    
    /**
     * 存放位置
     */
    private String location;
    
    /**
     * 異動類型
     */
    private Integer type;
    
    /**
     * 數量變化
     */
    private BigDecimal quantityChange;
    
    /**
     * 備註
     */
    private String note;
    
    // 預設建構子
    public CreateStockMovementInput() {
    }
    
    // 帶參數建構子
    public CreateStockMovementInput(Long itemId, String location, Integer type, BigDecimal quantityChange) {
        this.itemId = itemId;
        this.location = location;
        this.type = type;
        this.quantityChange = quantityChange;
    }
    
    // 帶完整參數建構子
    public CreateStockMovementInput(Long itemId, String location, Integer type, BigDecimal quantityChange, String note) {
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
        return "CreateStockMovementInput{" +
                "itemId=" + itemId +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", quantityChange=" + quantityChange +
                ", note='" + note + '\'' +
                '}';
    }
}