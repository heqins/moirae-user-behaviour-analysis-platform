package com.admin.server.helper;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
public class RedisHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void setIfAbsent(String key, String value) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public void setValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setValueWithExpire(String key, String value, Long nums, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, nums, timeUnit);
    }

    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public List<String> getHashValues(String key) {
        List<Object> values = stringRedisTemplate.opsForHash().values(key);
        return values.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public void putIfAbsentHashValue(String key, String hashKey, String hashValue) {
        if (key == null || hashKey == null) {
            throw new IllegalArgumentException("key or hashKey is null");
        }

        stringRedisTemplate.opsForHash().putIfAbsent(key, hashValue, hashValue);
    }

    public void hashSet(String key, Map<String, String> values) {

    }

    public void deleteValue(String key) {
        stringRedisTemplate.delete(key);
    }
}
