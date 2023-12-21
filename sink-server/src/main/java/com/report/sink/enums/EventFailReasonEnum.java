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

    KEY_FIELDS_MISSING("关键字段缺失"),

    UNKNOWN_ERROR("未知异常"),

    ;

    private final String reason;

    EventFailReasonEnum(String reason) {
        this.reason = reason;
    }

    public String gerReason() {
        return this.reason;
    }
}
