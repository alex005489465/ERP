package core.interface.controller;

import core.interface.controller.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

/**
 * 定義控制器層的回應操作與標準處理方式
 * 統一 API 回傳格式、錯誤處理等行為契約
 */
public interface IController {
    
    /**
     * 完整自定義回應
     * @param apiResponse API回應物件
     * @param httpStatusCode HTTP狀態碼
     * @param <T> 資料類型
     * @return ResponseEntity封裝的回應
     */
    <T> ResponseEntity<ApiResponse<T>> response(ApiResponse<T> apiResponse, int httpStatusCode);
    
    /**
     * 完整自定義回應（預設200狀態碼）
     * @param apiResponse API回應物件
     * @param <T> 資料類型
     * @return ResponseEntity封裝的回應
     */
    <T> ResponseEntity<ApiResponse<T>> response(ApiResponse<T> apiResponse);
    
    /**
     * 成功回應
     * @param data 資料物件
     * @param <T> 資料類型
     * @return 成功封裝的 ResponseEntity
     */
    <T> ResponseEntity<ApiResponse<T>> success(T data);
    
    /**
     * 錯誤回應
     * @param message 錯誤訊息
     * @return 錯誤封裝的 ResponseEntity
     */
    ResponseEntity<ApiResponse<Object>> error(String message);
    
    /**
     * 錯誤回應（含業務狀態碼）
     * @param message 錯誤訊息
     * @param businessCode 業務狀態碼
     * @return 錯誤封裝的 ResponseEntity
     */
    ResponseEntity<ApiResponse<Object>> error(String message, int businessCode);
}