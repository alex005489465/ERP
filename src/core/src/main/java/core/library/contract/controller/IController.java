package core.library.contract.controller;

/**
 * 定義控制器層的回應操作與標準處理方式
 * 統一 API 回傳格式、錯誤處理等行為契約
 */
public interface IController {
    
    /**
     * 完整自定義回應
     * @param data 資料物件
     * @param message 訊息
     * @param businessCode 業務狀態碼
     * @param httpStatusCode HTTP狀態碼
     * @param <T> 資料類型
     * @return 自定義封裝的 IApiResponse
     */
    <T> IApiResponse<T> response(T data, String message, int businessCode, int httpStatusCode);
    
    /**
     * 成功回應
     * @param data 資料物件
     * @param <T> 資料類型
     * @return 成功封裝的 IApiResponse
     */
    <T> IApiResponse<T> success(T data);
    
    /**
     * 失敗回應
     * @param message 錯誤訊息
     * @return 失敗封裝的 IApiResponse
     */
    IApiResponse<Object> failure(String message);
    
    /**
     * 失敗回應（含業務狀態碼）
     * @param message 錯誤訊息
     * @param businessCode 業務狀態碼
     * @return 失敗封裝的 IApiResponse
     */
    IApiResponse<Object> failure(String message, int businessCode);
}