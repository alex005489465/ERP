package erp.core.exception;

import erp.core.constant.ErrorCode;
import erp.core.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全域異常處理器
 * 統一處理所有控制器的異常，確保返回統一的錯誤格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 處理JSON解析錯誤
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("JSON解析錯誤: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error("請求格式錯誤，請檢查JSON格式", ErrorCode.INVALID_ARGUMENT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 處理方法參數類型不匹配錯誤
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("參數類型錯誤: {}", e.getMessage());
        ApiResponse<Void> response = ApiResponse.error("參數類型錯誤", ErrorCode.INVALID_ARGUMENT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 處理參數錯誤異常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("參數錯誤: {}", e.getMessage());
        
        String errorCode = ErrorCode.INVALID_ARGUMENT;
        if (e.getMessage().contains("商品不存在")) {
            errorCode = ErrorCode.ITEM_NOT_FOUND;
        }
        
        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), errorCode);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 處理狀態錯誤異常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException e) {
        log.warn("狀態錯誤: {}", e.getMessage());
        
        String errorCode = ErrorCode.INTERNAL_ERROR;
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        
        if (e.getMessage().contains("庫存不足")) {
            errorCode = ErrorCode.INSUFFICIENT_STOCK;
        } else if (e.getMessage().contains("庫存不存在")) {
            errorCode = ErrorCode.STOCK_NOT_FOUND;
            httpStatus = HttpStatus.NOT_FOUND;
        }
        
        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), errorCode);
        return ResponseEntity.status(httpStatus).body(response);
    }
    
    /**
     * 處理運行時異常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("運行時異常: ", e);
        ApiResponse<Void> response = ApiResponse.error("系統內部錯誤", ErrorCode.INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 處理所有其他異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("未預期的異常: ", e);
        ApiResponse<Void> response = ApiResponse.error("系統發生未預期錯誤", ErrorCode.UNEXPECTED_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}