package core.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis Repository 測試類
 * 驗證四個 Redis 接口的基本功能
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class RedisRepositoryTest {

    @Autowired
    private RedisRepository redisRepository;

    @Test
    void testRedisKeyOperations() {
        String testKey = "test:key:operations";
        
        // 測試 set 和 exists
        redisRepository.set(testKey, "test-value");
        assertTrue(redisRepository.exists(testKey));
        
        // 測試 get
        assertEquals("test-value", redisRepository.get(testKey));
        
        // 測試 expire 和 ttl
        redisRepository.expire(testKey, 60);
        Long ttl = redisRepository.ttl(testKey);
        assertTrue(ttl > 0 && ttl <= 60);
        
        // 測試 delete
        assertTrue(redisRepository.delete(testKey));
        assertFalse(redisRepository.exists(testKey));
    }

    @Test
    void testRedisStringOperations() {
        String testKey = "test:string:operations";
        
        // 測試 set with TTL
        redisRepository.set(testKey, "100", 60L);
        assertEquals("100", redisRepository.get(testKey));
        
        // 測試 incr 和 decr
        Long incrResult = redisRepository.incr(testKey);
        assertEquals(101L, incrResult);
        
        Long decrResult = redisRepository.decr(testKey);
        assertEquals(100L, decrResult);
        
        // 測試 append
        Integer appendResult = redisRepository.append(testKey, "0");
        assertEquals(4, appendResult); // "1000"
        assertEquals("1000", redisRepository.get(testKey));
        
        // 清理
        redisRepository.delete(testKey);
    }

    @Test
    void testRedisHashOperations() {
        String testKey = "test:hash:operations";
        
        // 測試 hset 和 hget
        redisRepository.hset(testKey, "field1", "value1");
        assertEquals("value1", redisRepository.hget(testKey, "field1"));
        
        // 測試 hexists
        assertTrue(redisRepository.hexists(testKey, "field1"));
        assertFalse(redisRepository.hexists(testKey, "nonexistent"));
        
        // 測試 hincrBy
        redisRepository.hset(testKey, "counter", "10");
        Long incrResult = redisRepository.hincrBy(testKey, "counter", 5);
        assertEquals(15L, incrResult);
        
        // 測試 hdecrBy (default method)
        Long decrResult = redisRepository.hdecrBy(testKey, "counter", 3);
        assertEquals(12L, decrResult);
        
        // 測試 hdel
        Long delResult = redisRepository.hdel(testKey, "field1");
        assertEquals(1L, delResult);
        assertFalse(redisRepository.hexists(testKey, "field1"));
        
        // 清理
        redisRepository.delete(testKey);
    }

    @Test
    void testRedisListOperations() {
        String testKey = "test:list:operations";
        
        // 測試 lpush 和 rpush
        Long lpushResult = redisRepository.lpush(testKey, "left1", "left2");
        assertEquals(2L, lpushResult);
        
        Long rpushResult = redisRepository.rpush(testKey, "right1", "right2");
        assertEquals(4L, rpushResult);
        
        // 測試 llen
        assertEquals(4L, redisRepository.llen(testKey));
        
        // 測試 lindex
        assertEquals("left2", redisRepository.lindex(testKey, 0));
        assertEquals("right2", redisRepository.lindex(testKey, 3));
        
        // 測試 lpop 和 rpop
        assertEquals("left2", redisRepository.lpop(testKey));
        assertEquals("right2", redisRepository.rpop(testKey));
        
        // 測試 lrange
        var rangeResult = redisRepository.lrange(testKey, 0, -1);
        assertEquals(2, rangeResult.size());
        
        // 清理
        redisRepository.delete(testKey);
    }
}