package com.report.sink.enums;

public enum EventStatusEnum {

    /**
     * 成功类型
     */
    SUCCESS(1),

    /**
     * 失败类型
     */
    FAIL(0);

    private final Integer status;

    EventStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public static boolean isStatusValid(Integer status) {
        EventStatusEnum[] values = EventStatusEnum.values();
        for (EventStatusEnum value: values) {
            if (value.getStatus().equals(status)) {
                return true;
            }
        }

        return false;
    }
}
