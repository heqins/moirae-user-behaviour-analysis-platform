package com.report.sink.enums;

/**
 * @author heqin
 */

public enum EventFailReasonEnum {

    /**
     *
     */
    DATA_ERROR("数据类型异常"),

    TABLE_NAME_ERROR("表名称生成失败"),

    RULE_CONTROL_ERROR("后台规则管控禁止"),


    ;

    private final String reason;

    EventFailReasonEnum(String reason) {
        this.reason = reason;
    }

    public String gerReason() {
        return this.reason;
    }
}
