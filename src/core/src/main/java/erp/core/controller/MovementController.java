package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiRequest;
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
    
    @PostMapping
    public ApiResponse<List<StockMovement>> handleMovementOperation(@RequestBody ApiRequest request) {
        try {
            String action = request.getAction();
            Map<String, Object> data = request.getData();
            
            if (action == null) {
                return ApiResponse.error("操作類型不能為空", ErrorCode.INVALID_ARGUMENT);
            }
            
            switch (action) {
                case "getByItem":
                    return handleGetByItem(data);
                case "getByLocation":
                    return handleGetByLocation(data);
                case "getRecent":
                    return handleGetRecent();
                default:
                    return ApiResponse.error("不支援的操作類型: " + action, ErrorCode.UNSUPPORTED_ACTION);
            }
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("處理異動記錄查詢時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
    /**
     * 查詢商品異動記錄
     */
    private ApiResponse<List<StockMovement>> handleGetByItem(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Object itemIdObj = data.get("itemId");
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        List<StockMovement> movements = warehouseService.getStockMovements(itemId);
        return ApiResponse.success("查詢成功", movements);
    }
    
    /**
     * 查詢位置異動記錄
     */
    private ApiResponse<List<StockMovement>> handleGetByLocation(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        String location = (String) data.get("location");
        if (location == null || location.trim().isEmpty()) {
            return ApiResponse.error("位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        List<StockMovement> movements = warehouseService.getStockMovementsByLocation(location.trim());
        return ApiResponse.success("查詢成功", movements);
    }
    
    /**
     * 查詢最近異動記錄
     */
    private ApiResponse<List<StockMovement>> handleGetRecent() {
        List<StockMovement> movements = warehouseService.getRecentStockMovements();
        return ApiResponse.success("查詢成功", movements);
    }
}