package erp.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 統一API請求格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
    
    /**
     * 操作類型
     */
    private String action;
    
    /**
     * 請求參數
     */
    private Map<String, Object> data;
}