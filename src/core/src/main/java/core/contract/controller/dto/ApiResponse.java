package core.contract.controller.dto;

public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private int businessCode;
    
    public ApiResponse() {
    }
    
    public ApiResponse(boolean success, String message, T data, int businessCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.businessCode = businessCode;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public int getBusinessCode() {
        return businessCode;
    }
    
    public void setBusinessCode(int businessCode) {
        this.businessCode = businessCode;
    }
}