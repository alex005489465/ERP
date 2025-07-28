package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiRequest;
import erp.core.dto.ApiResponse;
import erp.core.entity.Stock;
import erp.core.service.WarehouseManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 庫存查詢API控制器
 * 端點: /api/warehouse/stock
 */
@RestController
@RequestMapping("/api/warehouse/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {
    
    private final WarehouseManagementService warehouseService;
    
    @PostMapping
    public ApiResponse<?> handleStockOperation(@RequestBody ApiRequest request) {
        try {
            String action = request.getAction();
            Map<String, Object> data = request.getData();
            
            if (action == null) {
                return ApiResponse.error("操作類型不能為空", ErrorCode.INVALID_ARGUMENT);
            }
            
            switch (action) {
                case "getStock":
                    return handleGetStock(data);
                case "getTotalStock":
                    return handleGetTotalStock(data);
                case "getLowStocks":
                    return handleGetLowStocks(data);
                case "getZeroStocks":
                    return handleGetZeroStocks();
                default:
                    return ApiResponse.error("不支援的操作類型: " + action, ErrorCode.UNSUPPORTED_ACTION);
            }
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("處理庫存查詢時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
    /**
     * 查詢庫存
     * 支援三種查詢模式：
     * 1. 查詢特定商品在特定位置的庫存 (itemId + location)
     * 2. 查詢商品所有庫存 (僅 itemId)
     * 3. 查詢位置所有庫存 (僅 location)
     */
    private ApiResponse<?> handleGetStock(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Object itemIdObj = data.get("itemId");
        String location = (String) data.get("location");
        
        // 情況1: 查詢特定商品在特定位置的庫存
        if (itemIdObj != null && location != null && !location.trim().isEmpty()) {
            Long itemId;
            try {
                itemId = Long.valueOf(itemIdObj.toString());
            } catch (NumberFormatException e) {
                return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
            }
            
            Optional<Stock> stockOpt = warehouseService.getStock(itemId, location.trim());
            if (stockOpt.isPresent()) {
                return ApiResponse.success("查詢成功", stockOpt.get());
            } else {
                return ApiResponse.error("庫存不存在", ErrorCode.STOCK_NOT_FOUND);
            }
        }
        
        // 情況2: 查詢商品所有庫存
        if (itemIdObj != null) {
            Long itemId;
            try {
                itemId = Long.valueOf(itemIdObj.toString());
            } catch (NumberFormatException e) {
                return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
            }
            
            List<Stock> stocks = warehouseService.getStocksByItem(itemId);
            return ApiResponse.success("查詢成功", stocks);
        }
        
        // 情況3: 查詢位置所有庫存
        if (location != null && !location.trim().isEmpty()) {
            List<Stock> stocks = warehouseService.getStocksByLocation(location.trim());
            return ApiResponse.success("查詢成功", stocks);
        }
        
        return ApiResponse.error("必須提供商品ID或位置參數", ErrorCode.INVALID_ARGUMENT);
    }
    
    /**
     * 查詢商品總庫存量
     */
    private ApiResponse<BigDecimal> handleGetTotalStock(Map<String, Object> data) {
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
        
        try {
            BigDecimal totalStock = warehouseService.getTotalStock(itemId);
            return ApiResponse.success("查詢成功", totalStock);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        }
    }
    
    /**
     * 查詢低庫存商品
     */
    private ApiResponse<List<Stock>> handleGetLowStocks(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Object thresholdObj = data.get("threshold");
        if (thresholdObj == null) {
            return ApiResponse.error("庫存閾值不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        BigDecimal threshold;
        try {
            threshold = new BigDecimal(thresholdObj.toString());
            if (threshold.compareTo(BigDecimal.ZERO) < 0) {
                return ApiResponse.error("庫存閾值不能為負數", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("庫存閾值格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        List<Stock> lowStocks = warehouseService.getLowStocks(threshold);
        return ApiResponse.success("查詢成功", lowStocks);
    }
    
    /**
     * 查詢零庫存商品
     */
    private ApiResponse<List<Stock>> handleGetZeroStocks() {
        List<Stock> zeroStocks = warehouseService.getZeroStocks();
        return ApiResponse.success("查詢成功", zeroStocks);
    }
}