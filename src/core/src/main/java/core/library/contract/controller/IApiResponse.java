package core.library.contract.controller;

/**
 * 定義 API 回應物件的統一格式
 */
public interface IApiResponse<T> {
    
    /**
     * 獲取回應是否成功
     * @return 回應是否成功
     */
    boolean isSuccess();
    
    /**
     * 設置回應是否成功
     * @param success 回應是否成功
     */
    void setSuccess(boolean success);
    
    /**
     * 獲取提示訊息
     * @return 提示訊息，描述成功或錯誤原因
     */
    String getMessage();
    
    /**
     * 設置提示訊息
     * @param message 提示訊息
     */
    void setMessage(String message);
    
    /**
     * 獲取實際回傳資料
     * @return 實際回傳資料，可為任意型別
     */
    T getData();
    
    /**
     * 設置實際回傳資料
     * @param data 實際回傳資料
     */
    void setData(T data);
    
    /**
     * 獲取自訂業務代碼
     * @return 自訂業務代碼，例如 1001：帳號不存在
     */
    int getBusinessCode();
    
    /**
     * 設置自訂業務代碼
     * @param businessCode 自訂業務代碼
     */
    void setBusinessCode(int businessCode);
    
    /**
     * 獲取標準 HTTP 狀態碼
     * @return 標準 HTTP 狀態碼，例如 200、400、500
     */
    int getHttpStatusCode();
    
    /**
     * 設置標準 HTTP 狀態碼
     * @param httpStatusCode 標準 HTTP 狀態碼
     */
    void setHttpStatusCode(int httpStatusCode);
}