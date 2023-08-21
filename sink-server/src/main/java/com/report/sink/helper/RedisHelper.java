package com.report.sink.helper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Component
public class RedisHelper {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<String> getHashValues(String key) {
        List<Object> values = redisTemplate.opsForHash().values(key);
        return values.stream().map(String::valueOf).collect(Collectors.toList());
    }
}
