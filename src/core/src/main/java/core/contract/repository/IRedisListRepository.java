package core.contract.repository;

import java.util.List;

/**
 * Redis List 操作接口
 * 提供 Redis List 類型的基本操作方法
 */
public interface IRedisListRepository {
    
    /**
     * 將元素插入至列表左側
     * @param key 鍵名
     * @param values 要插入的值（可多個）
     * @return 插入後列表的長度
     */
    Long lpush(String key, String... values);
    
    /**
     * 將元素插入至列表右側
     * @param key 鍵名
     * @param values 要插入的值（可多個）
     * @return 插入後列表的長度
     */
    Long rpush(String key, String... values);
    
    /**
     * 移除並返回左側第一個元素
     * @param key 鍵名
     * @return 左側第一個元素，若列表為空則回傳 null
     */
    String lpop(String key);
    
    /**
     * 移除並返回右側第一個元素
     * @param key 鍵名
     * @return 右側第一個元素，若列表為空則回傳 null
     */
    String rpop(String key);
    
    /**
     * 取得指定範圍內元素
     * @param key 鍵名
     * @param start 開始索引（包含）
     * @param end 結束索引（包含），-1 表示到列表末尾
     * @return 指定範圍內的元素列表
     */
    List<String> lrange(String key, long start, long end);
    
    /**
     * 取得列表長度
     * @param key 鍵名
     * @return 列表長度
     */
    Long llen(String key);
    
    /**
     * 根據 index 取得元素
     * @param key 鍵名
     * @param index 索引位置
     * @return 指定索引的元素，若索引超出範圍則回傳 null
     */
    String lindex(String key, long index);
    
    /**
     * 移除指定數量的特定值
     * @param key 鍵名
     * @param count 移除數量（正數從左開始，負數從右開始，0 移除所有）
     * @param value 要移除的值
     * @return 實際移除的元素數量
     */
    Long lrem(String key, long count, String value);
    
    /**
     * 截取列表指定範圍
     * @param key 鍵名
     * @param start 開始索引（包含）
     * @param end 結束索引（包含）
     */
    void ltrim(String key, long start, long end);
}