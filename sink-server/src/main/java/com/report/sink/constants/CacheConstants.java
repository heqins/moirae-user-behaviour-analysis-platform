package com.report.sink.constants;

public class CacheConstants {

    public static final String APP_FIELD_CACHE_KEY = "field:app";

    public static String getAppFieldCacheKey(String appId) {
        return APP_FIELD_CACHE_KEY + ":" + appId;
    }
}
