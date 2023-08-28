package com.report.sink.constants;

public class CacheConstants {

    public static final String TABLE_COLUMN_CACHE_KEY = "table:column";

    public static String getColumnLocalCacheKey(String dbName, String tableName) {
        return "local:" + TABLE_COLUMN_CACHE_KEY + ":" + dbName + ":" + tableName;
    }
}
