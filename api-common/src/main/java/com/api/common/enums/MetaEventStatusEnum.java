package com.api.common.enums;

public enum MetaEventStatusEnum {
    ENABLE(0, "启用"),

    DISABLE(1, "禁用");

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
