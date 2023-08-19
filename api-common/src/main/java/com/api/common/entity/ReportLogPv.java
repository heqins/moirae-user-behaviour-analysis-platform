package com.api.common.entity;

public class ReportLogPv {

    private Long windowStart;

    private Long windowEnd;

    private Long count;

    public Long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(Long windowStart) {
        this.windowStart = windowStart;
    }

    public Long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(Long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public ReportLogPv(Long windowStart, Long windowEnd, Long count) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.count = count;
    }

    public ReportLogPv() {
    }

    @Override
    public String toString() {
        return "ReportLogPv{" +
                "windowStart=" + windowStart +
                ", windowEnd=" + windowEnd +
                ", count=" + count +
                '}';
    }
}
