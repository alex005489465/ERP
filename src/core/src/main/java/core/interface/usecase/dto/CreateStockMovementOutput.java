package core.interface.usecase.dto;


/**
 * 創建庫存異動的輸出結果 DTO
 */
public class CreateStockMovementOutput {
    
    
    /**
     * 操作是否成功
     */
    private boolean success;
    
    /**
     * 結果訊息
     */
    private String message;
    
    
    // 預設建構子
    public CreateStockMovementOutput() {
    }
    
    // 成功結果建構子
    public CreateStockMovementOutput(boolean success) {
        this.success = success;
        if (success) {
            this.message = "庫存異動記錄創建成功";
        }
    }
    
    // 失敗結果建構子
    public CreateStockMovementOutput(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
    }
    
    // 完整參數建構子
    public CreateStockMovementOutput(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getter 和 Setter 方法
    
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
    
    
    @Override
    public String toString() {
        return "CreateStockMovementOutput{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}