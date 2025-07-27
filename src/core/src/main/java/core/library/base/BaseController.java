package core.library.base;

import core.interface.controller.IController;
import core.interface.controller.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 基礎控制器抽象類別
 * 統一 API 回傳格式、錯誤處理等行為契約
 * 定義控制器層的回應操作與標準處理方式
 */
public abstract class BaseController implements IController {
    
    @Override
    public <T> ResponseEntity<ApiResponse<T>> response(ApiResponse<T> apiResponse, int httpStatusCode) {
        return ResponseEntity.status(httpStatusCode).body(apiResponse);
    }
    
    @Override
    public <T> ResponseEntity<ApiResponse<T>> response(ApiResponse<T> apiResponse) {
        return ResponseEntity.status(200).body(apiResponse);
    }
    
    @Override
    public <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(data);
        apiResponse.setMessage("操作成功");
        apiResponse.setBusinessCode(0);
        apiResponse.setSuccess(true);
        return ResponseEntity.ok(apiResponse);
    }
    
    @Override
    public ResponseEntity<ApiResponse<Object>> error(String message) {
        return error(message, -1);
    }
    
    @Override
    public ResponseEntity<ApiResponse<Object>> error(String message, int businessCode) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setData(null);
        apiResponse.setMessage(message);
        apiResponse.setBusinessCode(businessCode);
        apiResponse.setSuccess(false);
        return ResponseEntity.badRequest().body(apiResponse);
    }
}