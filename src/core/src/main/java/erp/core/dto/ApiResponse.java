package erp.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 統一API響應格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 操作是否成功
     */
    private boolean success;
    
    /**
     * 操作訊息
     */
    private String message;
    
    /**
     * 響應資料
     */
    private T data;
    
    /**
     * 錯誤代碼
     */
    private String errorCode;
    
    /**
     * 成功響應
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, null);
    }
    
    /**
     * 成功響應（帶自定義訊息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }
    
    /**
     * 失敗響應
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
    
    /**
     * 失敗響應（帶資料）
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return new ApiResponse<>(false, message, data, errorCode);
    }
}