package com.api.common.constant;

public class RedisCacheConstants {

    public static String getMetaEventCacheKey(String appId, String eventName) {
        return "meta-event:" + appId + ":" + eventName;
    }
}
