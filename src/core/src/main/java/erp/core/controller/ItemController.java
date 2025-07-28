package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiRequest;
import erp.core.dto.ApiResponse;
import erp.core.entity.Item;
import erp.core.service.WarehouseManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 商品管理API控制器
 * 端點: /api/warehouse/item
 */
@RestController
@RequestMapping("/api/warehouse/item")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    
    private final WarehouseManagementService warehouseService;
    
    @PostMapping
    public ApiResponse<?> handleItemOperation(@RequestBody ApiRequest request) {
        try {
            String action = request.getAction();
            Map<String, Object> data = request.getData();
            
            if (action == null) {
                return ApiResponse.error("操作類型不能為空", ErrorCode.INVALID_ARGUMENT);
            }
            
            switch (action) {
                case "create":
                    return handleCreate(data);
                case "update":
                    return handleUpdate(data);
                case "delete":
                    return handleDelete(data);
                case "get":
                    return handleGet(data);
                case "getAll":
                    return handleGetAll();
                case "search":
                    return handleSearch(data);
                default:
                    return ApiResponse.error("不支援的操作類型: " + action, ErrorCode.UNSUPPORTED_ACTION);
            }
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("處理商品操作時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
    /**
     * 創建商品
     */
    private ApiResponse<Item> handleCreate(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        String name = (String) data.get("name");
        String unit = (String) data.get("unit");
        
        if (name == null || name.trim().isEmpty()) {
            return ApiResponse.error("商品名稱不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (unit == null || unit.trim().isEmpty()) {
            return ApiResponse.error("商品單位不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Item item = warehouseService.createItem(name.trim(), unit.trim());
        return ApiResponse.success("商品創建成功", item);
    }
    
    /**
     * 更新商品
     */
    private ApiResponse<Item> handleUpdate(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Object itemIdObj = data.get("itemId");
        String name = (String) data.get("name");
        String unit = (String) data.get("unit");
        
        if (itemIdObj == null) {
            return ApiResponse.error("商品ID不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        Long itemId;
        try {
            itemId = Long.valueOf(itemIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.error("商品ID格式錯誤", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (name == null || name.trim().isEmpty()) {
            return ApiResponse.error("商品名稱不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        if (unit == null || unit.trim().isEmpty()) {
            return ApiResponse.error("商品單位不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        try {
            Item item = warehouseService.updateItem(itemId, name.trim(), unit.trim());
            return ApiResponse.success("商品更新成功", item);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        }
    }
    
    /**
     * 刪除商品
     */
    private ApiResponse<Void> handleDelete(Map<String, Object> data) {
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
            warehouseService.deleteItem(itemId);
            return ApiResponse.success("商品刪除成功", null);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("商品不存在")) {
                return ApiResponse.error(e.getMessage(), ErrorCode.ITEM_NOT_FOUND);
            }
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        }
    }
    
    /**
     * 查詢單一商品
     */
    private ApiResponse<Item> handleGet(Map<String, Object> data) {
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
        
        Optional<Item> itemOpt = warehouseService.getItem(itemId);
        if (itemOpt.isPresent()) {
            return ApiResponse.success("查詢成功", itemOpt.get());
        } else {
            return ApiResponse.error("商品不存在", ErrorCode.ITEM_NOT_FOUND);
        }
    }
    
    /**
     * 查詢所有商品
     */
    private ApiResponse<List<Item>> handleGetAll() {
        List<Item> items = warehouseService.getAllItems();
        return ApiResponse.success("查詢成功", items);
    }
    
    /**
     * 按名稱搜尋商品
     */
    private ApiResponse<List<Item>> handleSearch(Map<String, Object> data) {
        if (data == null) {
            return ApiResponse.error("請求資料不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        String name = (String) data.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ApiResponse.error("搜尋名稱不能為空", ErrorCode.INVALID_ARGUMENT);
        }
        
        List<Item> items = warehouseService.searchItemsByName(name.trim());
        return ApiResponse.success("搜尋成功", items);
    }
}