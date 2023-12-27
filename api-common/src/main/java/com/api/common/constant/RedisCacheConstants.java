package com.api.common.constant;

public class RedisCacheConstants {

    public static String generateKey(String prefix, Object... objs) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(":");
        for (int i = 0; i < objs.length; i++) {
            sb.append(objs[i]);
            if (i != objs.length - 1) {
                sb.append(":");
            }
        }

        return sb.toString();
    }

    public static String getMetaEventCacheKey(String appId, String eventName) {
        return generateKey("meta-event", appId, eventName);
    }

    public static String getMetaEventAttributeCacheKey(String appId, String eventName, String attributeName) {
        return generateKey("meta-event-attribute", appId, eventName, attributeName);
    }

    public static String getAppCacheKey(String appId) {
        return generateKey("app-v2", appId);
    }

    public static String getDorisColumnCacheKey(String dbName, String tableName) {
        return generateKey("doris-column", dbName, tableName);
    }
}
