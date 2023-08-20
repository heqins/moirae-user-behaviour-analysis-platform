package com.flink.job.util;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author heqin
 */
public class RedisUtils {

    public static Jedis jedis;

    static {
        //静态代码块初始化 redis
        jedis = new Jedis(PropertyUtils.getStrValue("redis.host"),
                PropertyUtils.getIntValue("redis.port"));
        jedis.select(PropertyUtils.getIntValue("redis.db"));
    }

    public static String getData(String s) {
        return jedis.get(s);
    }

    public static boolean putData(String key, String value) {
        return jedis.append(key, value) != null;
    }

    public static boolean rpush(String key, List<String> value) {
        for (String s : value) {
            jedis.rpush(key, s);
        }

        return true;
    }

    public static List<String> lrange(String key) {
        return jedis.lrange(key, 0, -1);
    }

}
