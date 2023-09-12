package com.api.common.enums;

public enum AnalysisCommandEnum {

    EVENT_COMMAND(1),

    FUNNEL_COMMAND(0),

    ;

    private Integer value;

    AnalysisCommandEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
