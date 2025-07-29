package erp.core.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * 單據明細資料傳輸物件
 */
@Setter
@Getter
public class SlipDetailDto {
    private Integer lineNumber;
    private Long itemId;
    private Long fromWarehouseId;
    private Long fromStorageLocationId;
    private Long toWarehouseId;
    private Long toStorageLocationId;
    private BigDecimal quantityChange;
    private String note;

    // Constructors
    public SlipDetailDto() {}

    public SlipDetailDto(Integer lineNumber, Long itemId, Long fromWarehouseId, 
                        Long fromStorageLocationId, Long toWarehouseId, 
                        Long toStorageLocationId, BigDecimal quantityChange, 
                        String note) {
        this.lineNumber = lineNumber;
        this.itemId = itemId;
        this.fromWarehouseId = fromWarehouseId;
        this.fromStorageLocationId = fromStorageLocationId;
        this.toWarehouseId = toWarehouseId;
        this.toStorageLocationId = toStorageLocationId;
        this.quantityChange = quantityChange;
        this.note = note;
    }
}