package core.library.contract.entity;

import java.time.LocalDateTime;

/**
 * 所有資料實體類別應實作此接口
 * 統一 ID、時間欄位等標準欄位與行為
 */
public interface IEntity {
    
    /**
     * 獲取唯一識別碼
     * @return 實體的唯一識別碼
     */
    Long getId();
    
    /**
     * 設置唯一識別碼
     * @param id 唯一識別碼
     */
    void setId(Long id);
    
    /**
     * 獲取建立時間
     * @return 實體建立時間
     */
    LocalDateTime getCreatedAt();
    
    /**
     * 設置建立時間
     * @param createdAt 建立時間
     */
    void setCreatedAt(LocalDateTime createdAt);
    
    /**
     * 獲取更新時間
     * @return 實體最後更新時間
     */
    LocalDateTime getUpdatedAt();
    
    /**
     * 設置更新時間
     * @param updatedAt 更新時間
     */
    void setUpdatedAt(LocalDateTime updatedAt);
}