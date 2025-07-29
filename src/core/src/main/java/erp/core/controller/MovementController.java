package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiResponse;
import erp.core.entity.StockMovement;
import erp.core.service.WarehouseManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 庫存異動記錄查詢API控制器
 * 端點: /api/warehouse/movement
 */
@RestController
@RequestMapping("/api/warehouse/movement")
@RequiredArgsConstructor
@Slf4j
public class MovementController {
    
    private final WarehouseManagementService warehouseService;
    
    /**
     * 查詢商品異動記錄
     */
    @PostMapping("/byItem")
    public ApiResponse<List<StockMovement>> getMovementsByItem(@RequestBody Map<String, Object> data) {
        try {
            Long itemId = null;
            if (data != null) {
                Object itemIdObj = data.get("itemId");
                if (itemIdObj != null) {
                    itemId = Long.valueOf(itemIdObj.toString());
                }
            }
            
            if (itemId == null) {
                return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
            }
            
            List<StockMovement> movements = warehouseService.getStockMovements(itemId);
            return ApiResponse.success("查詢成功", movements);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢商品異動記錄時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 查詢位置異動記錄
     */
    @PostMapping("/byLocation")
    public ApiResponse<List<StockMovement>> getMovementsByLocation(@RequestBody Map<String, Object> data) {
        try {
            String location = null;
            if (data != null) {
                location = (String) data.get("location");
            }
            
            if (location == null || location.trim().isEmpty()) {
                return ApiResponse.error("位置不能為空", ErrorCode.INVALID_ARGUMENT);
            }
            
            List<StockMovement> movements = warehouseService.getStockMovementsByLocation(location.trim());
            return ApiResponse.success("查詢成功", movements);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢位置異動記錄時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 查詢最近異動記錄
     */
    @PostMapping("/recent")
    public ApiResponse<List<StockMovement>> getRecentMovements() {
        try {
            List<StockMovement> movements = warehouseService.getRecentStockMovements();
            return ApiResponse.success("查詢成功", movements);
        } catch (Exception e) {
            log.error("查詢最近異動記錄時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
}