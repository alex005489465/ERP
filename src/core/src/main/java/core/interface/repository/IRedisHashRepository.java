package core.interface.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis Hash 操作接口
 * 提供 Redis Hash 類型的基本操作方法
 */
public interface IRedisHashRepository {
    
    /**
     * 設定單一欄位值
     * @param key 鍵名
     * @param field 欄位名
     * @param value 欄位值
     */
    void hset(String key, String field, String value);
    
    /**
     * 取得欄位值
     * @param key 鍵名
     * @param field 欄位名
     * @return 欄位值，若欄位不存在則回傳 null
     */
    String hget(String key, String field);
    
    /**
     * 移除欄位
     * @param key 鍵名
     * @param fields 要移除的欄位名（可多個）
     * @return 實際移除的欄位數量
     */
    Long hdel(String key, String... fields);
    
    /**
     * 取得整個 Hash
     * @param key 鍵名
     * @return Hash 的所有欄位和值的 Map
     */
    Map<String, String> hgetAll(String key);
    
    /**
     * 取得所有欄位名稱
     * @param key 鍵名
     * @return 所有欄位名稱的集合
     */
    Set<String> hfields(String key);
    
    /**
     * 取得所有欄位值
     * @param key 鍵名
     * @return 所有欄位值的列表
     */
    List<String> hvals(String key);
    
    /**
     * 檢查欄位是否存在
     * @param key 鍵名
     * @param field 欄位名
     * @return 欄位是否存在
     */
    Boolean hexists(String key, String field);
    
    /**
     * 對數值欄位遞增
     * @param key 鍵名
     * @param field 欄位名
     * @param increment 遞增值
     * @return 遞增後的值
     */
    Long hincrBy(String key, String field, long increment);
    
    /**
     * 對數值欄位遞減
     * @param key 鍵名
     * @param field 欄位名
     * @param increment 遞減值（正數）
     * @return 遞減後的值
     */
    default Long hdecrBy(String key, String field, long increment) {
        return hincrBy(key, field, -increment);
    }
}