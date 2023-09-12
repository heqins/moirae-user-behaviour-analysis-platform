package com.api.common.enums;

public enum MetaEventAttributeStatusEnum {
    ENABLE(1, "开启"),

    DISABLE(0, "关闭");

    private Integer status;

    private String description;

    MetaEventAttributeStatusEnum(Integer status, String description) {
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
