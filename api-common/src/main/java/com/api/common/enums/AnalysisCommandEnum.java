package com.api.common.enums;

public enum AnalysisCommandEnum {

    EVENT_COMMAND("eventHandler"),

    EVENT_COUNT_COMMAND("eventCountHandler"),

    EVENT_RETENTION_COMMAND("retentionAnalysisHandler"),

    ;

    private String value;

    AnalysisCommandEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
