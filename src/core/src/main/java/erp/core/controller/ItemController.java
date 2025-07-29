package erp.core.controller;

import erp.core.constant.ErrorCode;
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
    
    /**
     * 創建商品
     */
    @PostMapping("/create")
    public ApiResponse<Item> createItem(@RequestBody Map<String, Object> data) {
        try {
            return handleCreate(data);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("創建商品時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 更新商品
     */
    @PostMapping("/update")
    public ApiResponse<Item> updateItem(@RequestBody Map<String, Object> data) {
        try {
            return handleUpdate(data);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("更新商品時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 刪除商品
     */
    @PostMapping("/delete")
    public ApiResponse<Void> deleteItem(@RequestBody Map<String, Object> data) {
        try {
            return handleDelete(data);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("刪除商品時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 查詢商品資訊
     * 支援三種查詢模式：
     * 1. 查詢單一商品 (提供 id 參數)
     * 2. 查詢所有商品 (不提供任何參數)
     * 3. 按名稱模糊搜尋 (提供 name 參數)
     */
    @PostMapping("/info")
    public ApiResponse<?> getItemInfo(@RequestBody(required = false) Map<String, Object> data) {
        try {
            Long id = null;
            String name = null;
            
            // 從請求體中提取參數
            if (data != null) {
                Object idObj = data.get("id");
                if (idObj != null) {
                    id = Long.valueOf(idObj.toString());
                }
                name = (String) data.get("name");
            }
            
            // 情況1: 查詢單一商品
            if (id != null) {
                Optional<Item> itemOpt = warehouseService.getItem(id);
                if (itemOpt.isPresent()) {
                    return ApiResponse.success("查詢成功", itemOpt.get());
                } else {
                    return ApiResponse.error("商品不存在", ErrorCode.ITEM_NOT_FOUND);
                }
            }
            
            // 情況3: 按名稱模糊搜尋
            if (name != null && !name.trim().isEmpty()) {
                List<Item> items = warehouseService.searchItemsByName(name.trim());
                return ApiResponse.success("搜尋成功", items);
            }
            
            // 情況2: 查詢所有商品
            List<Item> items = warehouseService.getAllItems();
            return ApiResponse.success("查詢成功", items);
            
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("查詢商品資訊時發生未預期錯誤", e);
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
    
}