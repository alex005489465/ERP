package core.library.contract.repository;

import java.util.Set;

/**
 * Redis 通用鍵操作接口
 * 提供 Redis 鍵的基本操作方法
 */
public interface IRedisKeyRepository {
    
    /**
     * 檢查鍵是否存在
     * @param key 鍵名
     * @return 鍵是否存在
     */
    Boolean exists(String key);
    
    /**
     * 刪除鍵（不論其類型）
     * @param key 鍵名
     * @return 刪除是否成功
     */
    Boolean delete(String key);
    
    /**
     * 設定過期時間
     * @param key 鍵名
     * @param ttl 過期時間（秒）
     * @return 設定是否成功
     */
    Boolean expire(String key, long ttl);
    
    /**
     * 移除過期時間
     * @param key 鍵名
     * @return 移除是否成功
     */
    Boolean persist(String key);
    
    /**
     * 取得剩餘生存時間
     * @param key 鍵名
     * @return 剩餘生存時間（秒），-1 表示永不過期，-2 表示鍵不存在
     */
    Long ttl(String key);
    
    /**
     * 取得所有符合 pattern 的鍵名（支援 *, ? 等）
     * @param pattern 匹配模式
     * @return 符合條件的鍵名集合
     */
    Set<String> keys(String pattern);
}