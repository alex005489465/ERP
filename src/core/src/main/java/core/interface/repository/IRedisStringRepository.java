package core.interface.repository;

/**
 * Redis 字串操作接口
 * 提供 Redis String 類型的基本操作方法
 */
public interface IRedisStringRepository {
    
    /**
     * 設定字串值並指定過期時間
     * @param key 鍵名
     * @param value 值
     * @param ttl 過期時間（秒），null 表示不設定過期時間
     */
    void set(String key, String value, Long ttl);
    
    /**
     * 設定字串值（不過期）
     * @param key 鍵名
     * @param value 值
     */
    default void set(String key, String value) {
        set(key, value, null);
    }
    
    /**
     * 取得字串值
     * @param key 鍵名
     * @return 字串值，若鍵不存在則回傳 null
     */
    String get(String key);
    
    /**
     * 整數遞增
     * @param key 鍵名
     * @return 遞增後的值
     */
    Long incr(String key);
    
    /**
     * 整數遞減
     * @param key 鍵名
     * @return 遞減後的值
     */
    Long decr(String key);
    
    /**
     * 附加字串
     * @param key 鍵名
     * @param value 要附加的字串
     * @return 附加後字串的總長度
     */
    Integer append(String key, String value);
}