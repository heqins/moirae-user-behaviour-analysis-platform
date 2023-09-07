package com.api.common.enums;

public enum MetaEventStatusEnum {
    ENABLE(1, "启用"),

    DISABLE(0, "禁用");

    private Integer status;

    private String description;

    MetaEventStatusEnum(Integer status, String description) {
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
