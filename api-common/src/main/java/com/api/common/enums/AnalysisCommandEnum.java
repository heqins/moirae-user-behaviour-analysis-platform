package com.api.common.enums;

public enum AnalysisCommandEnum {

    EVENT_COMMAND("eventHandler"),

    FUNNEL_COMMAND(""),

    ;

    private String value;

    AnalysisCommandEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
