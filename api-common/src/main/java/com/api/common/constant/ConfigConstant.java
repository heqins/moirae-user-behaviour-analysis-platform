package com.api.common.constant;

public interface ConfigConstant {
    String EVENT_TABLE_PREFIX = "event_log_detail_";

    static String generateTableName(String appId) {
        return EVENT_TABLE_PREFIX + appId;
    }

    String CONFIG_FILE_PATH = "config.properties";

    public interface Topics {
        public static final String LOG_ETL_MAIN_TOPIC = "log-etl-main";

        public static final String LOG_SINK_TOPIC = "log-sink";

        public static final String REPORT_LOG_DATA_TOPIC = "report-log-data";

        public static final String LOG_DATA_INVALID_TOPIC = "report-log-invalid-data";
    }

    public interface GroupIds {
        public static final String REPORT_MAIN_GROUP_ID = "report-main-flink-dev";

        public static final String LOG_ETL_MAIN__GROUP_ID = "log-etl-flink-dev";

    }
}
