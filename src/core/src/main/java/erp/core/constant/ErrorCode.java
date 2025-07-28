package erp.core.constant;

/**
 * API錯誤代碼常量
 */
public class ErrorCode {
    
    /**
     * 不支援的操作類型
     */
    public static final String UNSUPPORTED_ACTION = "UNSUPPORTED_ACTION";
    
    /**
     * 參數錯誤或缺失
     */
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    
    /**
     * 商品不存在
     */
    public static final String ITEM_NOT_FOUND = "ITEM_NOT_FOUND";
    
    /**
     * 庫存不存在
     */
    public static final String STOCK_NOT_FOUND = "STOCK_NOT_FOUND";
    
    /**
     * 庫存不足
     */
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    
    /**
     * 系統內部錯誤
     */
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    
    /**
     * 未預期的錯誤
     */
    public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
    
    private ErrorCode() {
        // 私有構造函數，防止實例化
    }
}