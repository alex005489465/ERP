package erp.core.controller;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiResponse;
import erp.core.entity.Slip;
import erp.core.service.SlipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/slips")
@RequiredArgsConstructor
@Slf4j
public class SlipController {

    private final SlipService slipService;

    /**
     * 創建單據
     */
    @PostMapping
    public ApiResponse<Slip> createSlip(@RequestBody Map<String, Object> data) {
        try {
            log.info("創建單據請求: {}", data);
            
            // TODO: 實現創建單據邏輯
            
            return ApiResponse.success("單據創建成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("創建單據時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 狀態操作
     */
    @PostMapping("/status")
    public ApiResponse<Slip> updateSlipStatus(@RequestBody Map<String, Object> data) {
        try {
            Object idObj = data.get("id");
            Long id = null;
            if (idObj != null) {
                if (idObj instanceof Number) {
                    id = ((Number) idObj).longValue();
                } else if (idObj instanceof String) {
                    id = Long.parseLong((String) idObj);
                }
            }
            
            log.info("單據狀態操作請求 - ID: {}, 資料: {}", id, data);
            
            // TODO: 實現狀態操作邏輯
            
            return ApiResponse.success("單據狀態更新成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (IllegalStateException e) {
            log.warn("狀態錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch (Exception e) {
            log.error("更新單據狀態時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }

    /**
     * 讀取單據
     */
    @PostMapping("/read")
    public ApiResponse<Slip> readSlip(@RequestBody Map<String, Object> data) {
        try {
            log.info("讀取單據請求: {}", data);
            
            // TODO: 實現讀取單據邏輯
            
            return ApiResponse.success("單據讀取成功", null);
        } catch (IllegalArgumentException e) {
            log.warn("參數錯誤: {}", e.getMessage());
            return ApiResponse.error(e.getMessage(), ErrorCode.INVALID_ARGUMENT);
        } catch (Exception e) {
            log.error("讀取單據時發生未預期錯誤", e);
            return ApiResponse.error("系統內部錯誤", ErrorCode.UNEXPECTED_ERROR);
        }
    }
}