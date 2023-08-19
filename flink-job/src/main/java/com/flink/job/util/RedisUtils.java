package com.flink.job.util;

import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisUtils {

    public static Jedis jedis;

    //静态代码块初始化 redis
    static {
        jedis = new Jedis(PropertyUtils.getStrValue("redis.host"), PropertyUtils.getIntegerValue("redis.port"));
        jedis.select(PropertyUtils.getIntegerValue("redis.db"));
    }

    public static String getData(String s) {
        return jedis.get(s);
    }

    public static boolean putData(String key, String value) {
        return jedis.append(key, value) != null;
    }

    public static boolean rpush(String key, List<String> value) {
        for(int i = 0; i < value.size(); i++) {
            jedis.rpush(key, value.get(i));
        }
        return true;
    }

    public static List<String> lrange(String key) {
        return jedis.lrange(key, 0, -1);
    }

}
