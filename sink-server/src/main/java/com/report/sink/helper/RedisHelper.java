package com.report.sink.helper;

import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, String> redisTemplate;

    public void setIfAbsent(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValueWithExpire(String key, String value, Long nums, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, nums, timeUnit);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<String> getHashValues(String key) {
        List<Object> values = redisTemplate.opsForHash().values(key);
        return values.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public void putHashValue(String key, String hashKey, String hashValue) {
        if (key == null || hashKey == null) {
            throw new IllegalArgumentException("key or hashKey is null");
        }

        redisTemplate.opsForHash().put(key, hashValue, hashValue);
    }

    public void deleteHashKey(String key, String hashKey) {
        if (key == null || hashKey == null) {
            throw new IllegalArgumentException("key or hashKey is null");
        }

        redisTemplate.opsForHash().delete(key, hashKey);
    }
    public List<String> multiGet(List<String> keyList) {
        return redisTemplate.opsForValue().multiGet(keyList);
    }
}
