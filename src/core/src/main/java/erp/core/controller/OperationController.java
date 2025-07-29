package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiResponse;
import erp.core.service.WarehouseManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 庫存操作API控制器
 * 端點: /api/warehouse/operation
 */
@RestController
@RequestMapping("/api/warehouse/operation")
@RequiredArgsConstructor
@Slf4j
public class OperationController {
    
    private final WarehouseManagementService warehouseService;
    
    /**
     * 一般庫存操作 (入庫、出庫、凍結、報廢、解凍)
     */
    @PostMapping("/operation")
    public ApiResponse<Void> handleGeneralOperation(@RequestBody Map<String, Object> request) {
        try {
            String action = (String) request.get("action");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.get("data");
            
            if (action == null) {
                return ApiResponse.error("操作類型不能為空", ErrorCode.INVALID_ARGUMENT);
            }

            return switch (action) {
                case "inbound" -> handleInbound(data);
                case "outbound" -> handleOutbound(data);
                case "freeze" -> handleFreeze(data);
                case "scrap" -> handleScrap(data);
                case "unfreeze" -> handleUnfreeze(data);
                default -> ApiResponse.error("不支援的操作類型: " + action, ErrorCode.UNSUPPORTED_ACTION);
            };
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            if (e.getMessage().contains("庫存不足")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.INSUFFICIENT_STOCK);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("處理庫存操作時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 轉庫操作 (需要來源位置和目標位置)
     */
    @PostMapping("/transfer")
    public ApiResponse<Void> handleTransferOperation(@RequestBody Map<String, Object> data) {
        try {
            return handleTransfer(data);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            if (e.getMessage().contains("庫存不足")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.INSUFFICIENT_STOCK);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("處理轉庫操作時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
    /**
     * 入庫操作
     */
    private ApiResponse<Void> handleInbound(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String location = (String) data.get("location");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (location == null || location.trim().isEmpty()) {
            return ApiResponse.error("位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.inbound(itemId, location.trim(), quantity, note);
        return ApiResponse.success("入庫操作成功", null);
    }
    
    /**
     * 出庫操作
     */
    private ApiResponse<Void> handleOutbound(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String location = (String) data.get("location");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (location == null || location.trim().isEmpty()) {
            return ApiResponse.error("位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.outbound(itemId, location.trim(), quantity, note);
        return ApiResponse.success("出庫操作成功", null);
    }
    
    /**
     * 轉庫操作
     */
    private ApiResponse<Void> handleTransfer(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String fromLocation = (String) data.get("fromLocation");
        String toLocation = (String) data.get("toLocation");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            return ApiResponse.error("來源位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (toLocation == null || toLocation.trim().isEmpty()) {
            return ApiResponse.error("目標位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.transfer(itemId, fromLocation.trim(), toLocation.trim(), quantity, note);
        return ApiResponse.success("轉庫操作成功", null);
    }
    
    /**
     * 凍結操作
     */
    private ApiResponse<Void> handleFreeze(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String fromLocation = (String) data.get("fromLocation");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            return ApiResponse.error("來源位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.freeze(itemId, fromLocation.trim(), quantity, note);
        return ApiResponse.success("凍結操作成功", null);
    }
    
    /**
     * 報廢操作
     */
    private ApiResponse<Void> handleScrap(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String fromLocation = (String) data.get("fromLocation");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (fromLocation == null || fromLocation.trim().isEmpty()) {
            return ApiResponse.error("來源位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.scrap(itemId, fromLocation.trim(), quantity, note);
        return ApiResponse.success("報廢操作成功", null);
    }
    
    /**
     * 解凍操作
     */
    private ApiResponse<Void> handleUnfreeze(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        // 驗證必要參數
        Object itemIdObj = data.get("itemId");
        String toLocation = (String) data.get("toLocation");
        Object quantityObj = data.get("quantity");
        String note = (String) data.get("note"); // 選用參數
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (toLocation == null || toLocation.trim().isEmpty()) {
            return ApiResponse.error("目標位置不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (quantityObj == null) {
            return ApiResponse.error("數量不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        BigDecimal quantity;
        
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            quantity = new BigDecimal(quantityObj.toString());
            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("數量必須大於0", ErrorCode.INVALID_ARGUMENT);
            }
        } catch (NumberFormatException e) {
            return ApiResponse.error("數量格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        warehouseService.unfreeze(itemId, toLocation.trim(), quantity, note);
        return ApiResponse.success("解凍操作成功", null);
    }
}