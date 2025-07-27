package core.contract.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 基礎資料存取接口
 * 定義所有 Repository 的基本 CRUD 操作方法
 * 
 * @param <T> 實體類型
 * @param <ID> 主鍵類型
 */
public interface IBaseRepository<T, ID> {
    
    /**
     * 查詢所有資料
     * @return 所有資料列表
     */
    List<T> find();
    
    /**
     * 查詢符合條件的第一筆資料
     * @param conditions 查詢條件 Map
     * @return 符合條件的第一筆資料，若無則回傳 empty
     */
    Optional<T> findFirst(Map<String, Object> conditions);
    
    /**
     * 根據主鍵 ID 查詢單筆資料
     * @param id 主鍵 ID
     * @return 對應的資料，若無則回傳 empty
     */
    Optional<T> findById(ID id);
    
    /**
     * 新增或更新資料（合併 create/update）
     * @param entity 要儲存的實體
     * @return 儲存後的實體
     */
    T save(T entity);
    
    /**
     * 刪除資料
     * @param entity 要刪除的實體
     * @return 刪除是否成功
     */
    boolean delete(T entity);
    
    /**
     * 判斷是否存在符合條件的資料
     * @param conditions 查詢條件 Map
     * @return 是否存在符合條件的資料
     */
    boolean exists(Map<String, Object> conditions);
    
    /**
     * 計算符合條件的資料筆數
     * @param conditions 查詢條件 Map
     * @return 符合條件的資料筆數
     */
    long count(Map<String, Object> conditions);
}