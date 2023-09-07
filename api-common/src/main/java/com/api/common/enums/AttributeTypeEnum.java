package com.api.common.enums;

public enum AttributeTypeEnum {

    SYSTEM(1, "系统属性"),
    USER_CUSTOM(2, "用户自定义属性"),

    ;

    private Integer status;

    private String description;

    AttributeTypeEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
