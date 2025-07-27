package core.repository.redis;

import core.contract.repository.IRedisKeyRepository;
import core.contract.repository.IRedisStringRepository;
import core.contract.repository.IRedisHashRepository;
import core.contract.repository.IRedisListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作實作類
 * 實作四個 Redis 操作接口：Key、String、Hash、List
 */
@Repository
public class RedisRepository implements IRedisKeyRepository, IRedisStringRepository, IRedisHashRepository, IRedisListRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //region IRedisKeyRepository 實作

    @Override
    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    @Override
    public Boolean expire(String key, long ttl) {
        return stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    @Override
    public Boolean persist(String key) {
        return stringRedisTemplate.persist(key);
    }

    @Override
    public Long ttl(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    //endregion

    //region IRedisStringRepository 實作

    @Override
    public void set(String key, String value, Long ttl) {
        if (ttl != null && ttl > 0) {
            stringRedisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Long incr(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long decr(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    @Override
    public Integer append(String key, String value) {
        return stringRedisTemplate.opsForValue().append(key, value);
    }

    //endregion

    //region IRedisHashRepository 實作

    @Override
    public void hset(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public String hget(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    @Override
    public Long hdel(String key, String... fields) {
        return stringRedisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        return entries.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue() != null ? entry.getValue().toString() : null
                ));
    }

    @Override
    public Set<String> hfields(String key) {
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(key);
        return keys.stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public List<String> hvals(String key) {
        List<Object> values = stringRedisTemplate.opsForHash().values(key);
        return values.stream()
                .map(value -> value != null ? value.toString() : null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Boolean hexists(String key, String field) {
        return stringRedisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public Long hincrBy(String key, String field, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, field, increment);
    }

    //endregion

    //region IRedisListRepository 實作

    @Override
    public Long lpush(String key, String... values) {
        return stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    @Override
    public Long rpush(String key, String... values) {
        return stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public String lpop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    @Override
    public String rpop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long llen(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    @Override
    public String lindex(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return stringRedisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public void ltrim(String key, long start, long end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }

    //endregion
}