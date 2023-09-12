package com.api.common.constant;

public class SinkConstants {

    static String EVENT_TABLE_PREFIX = "event_log_detail_";

    public static String generateTableName(String appId) {
        return EVENT_TABLE_PREFIX + appId;
    }

}
