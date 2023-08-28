package com.flink.job.model.entity;

public class EventLogPv {

    private Long windowStart;

    private Long windowEnd;

    private Long count;

    public String getAppName() {
        return appName;
    }

    private String appName;

    public EventLogPv(Long windowStart, Long windowEnd, Long count, String appName) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.count = count;
        this.appName = appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }


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

    public EventLogPv() {
    }

    @Override
    public String toString() {
        return "ReportLogPv{" +
                "windowStart=" + windowStart +
                ", windowEnd=" + windowEnd +
                ", count=" + count +
                ", appName='" + appName + '\'' +
                '}';
    }
}
