package core.library.base;

import core.library.contract.controller.IController;
import core.library.contract.controller.IApiResponse;

/**
 * 基礎控制器抽象類別
 * 統一 API 回傳格式、錯誤處理等行為契約
 * 定義控制器層的回應操作與標準處理方式
 */
public abstract class BaseController implements IController {
    
    /**
     * 建立 API 回應物件的具體實作
     * 子類別需要提供具體的 IApiResponse 實作
     * @param <T> 資料類型
     * @return IApiResponse 實例
     */
    protected abstract <T> IApiResponse<T> createApiResponse();
    
    @Override
    public <T> IApiResponse<T> response(T data, String message, int businessCode, int httpStatusCode) {
        IApiResponse<T> response = createApiResponse();
        response.setData(data);
        response.setMessage(message);
        response.setBusinessCode(businessCode);
        response.setHttpStatusCode(httpStatusCode);
        response.setSuccess(httpStatusCode >= 200 && httpStatusCode < 300);
        return response;
    }
    
    @Override
    public <T> IApiResponse<T> success(T data) {
        IApiResponse<T> response = createApiResponse();
        response.setData(data);
        response.setMessage("操作成功");
        response.setBusinessCode(0);
        response.setHttpStatusCode(200);
        response.setSuccess(true);
        return response;
    }
    
    @Override
    public IApiResponse<Object> failure(String message) {
        return failure(message, -1);
    }
    
    @Override
    public IApiResponse<Object> failure(String message, int businessCode) {
        IApiResponse<Object> response = createApiResponse();
        response.setData(null);
        response.setMessage(message);
        response.setBusinessCode(businessCode);
        response.setHttpStatusCode(400);
        response.setSuccess(false);
        return response;
    }
}