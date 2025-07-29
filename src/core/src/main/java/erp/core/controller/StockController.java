package erp.core.controller;

import erp.core.constant.ErrorCode;
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
    
    /**
     * 查詢庫存
     * 支援三種查詢模式：
     * 1. 查詢特定商品在特定位置的庫存 (itemId + location)
     * 2. 查詢商品所有庫存 (僅 itemId)
     * 3. 查詢位置所有庫存 (僅 location)
     */
    @PostMapping("/stock")
    public ApiResponse<?> getStock(@RequestBody(required = false) Map<String, Object> data) {
        try {
            Long itemId = null;
            String location = null;
            
            // 從請求體中提取參數
            if (data != null) {
                Object itemIdObj = data.get("itemId");
                if (itemIdObj != null) {
                    itemId = Long.valueOf(itemIdObj.toString());
                }
                location = (String) data.get("location");
            }
            
            // 情況1: 查詢特定商品在特定位置的庫存
            if (itemId != null && location != null && !location.trim().isEmpty()) {
                Optional<Stock> stockOpt = warehouseService.getStock(itemId, location.trim());
                if (stockOpt.isPresent()) {
                    return ApiResponse.success("查詢成功", stockOpt.get());
                } else {
                    return ApiResponse.error("庫存不存在", ErrorCode.STOCK_NOT_FOUND);
                }
            }
            
            // 情況2: 查詢商品所有庫存
            if (itemId != null) {
                List<Stock> stocks = warehouseService.getStocksByItem(itemId);
                return ApiResponse.success("查詢成功", stocks);
            }
            
            // 情況3: 查詢位置所有庫存
            if (location != null && !location.trim().isEmpty()) {
                List<Stock> stocks = warehouseService.getStocksByLocation(location.trim());
                return ApiResponse.success("查詢成功", stocks);
            }
            
            return ApiResponse.error("必須提供商品ID或位置參數", ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢庫存時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 查詢商品總庫存量
     */
    @PostMapping("/totalStock")
    public ApiResponse<BigDecimal> getTotalStock(@RequestBody Map<String, Object> data) {
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
            
            BigDecimal totalStock = warehouseService.getTotalStock(itemId);
            return ApiResponse.success("查詢成功", totalStock);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢總庫存時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 查詢低庫存和零庫存商品
     * 如果提供threshold參數，查詢低於該閾值的庫存
     * 如果不提供threshold參數，查詢零庫存商品
     */
    @PostMapping("/lowAndZeroStocks")
    public ApiResponse<List<Stock>> getLowAndZeroStocks(@RequestBody(required = false) Map<String, Object> data) {
        try {
            BigDecimal threshold = null;
            if (data != null) {
                Object thresholdObj = data.get("threshold");
                if (thresholdObj != null) {
                    threshold = new BigDecimal(thresholdObj.toString());
                }
            }
            
            List<Stock> stocks;
            
            if (threshold != null) {
                // 查詢低庫存商品
                if (threshold.compareTo(BigDecimal.ZERO) < 0) {
                    return ApiResponse.error("庫存閾值不能為負數", ErrorCode.INVALID_ARGUMENT);
                }
                stocks = warehouseService.getLowStocks(threshold);
                return ApiResponse.success("查詢低庫存成功", stocks);
            } else {
                // 查詢零庫存商品
                stocks = warehouseService.getZeroStocks();
                return ApiResponse.success("查詢零庫存成功", stocks);
            }
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢低庫存/零庫存時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
}