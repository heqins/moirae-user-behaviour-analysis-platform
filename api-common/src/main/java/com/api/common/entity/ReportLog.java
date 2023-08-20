package com.api.common.entity;

/**
 * @author heqin
 */
public class ReportLog {

    private String appName;

    private String appVersion;

    private String eventName;

    private Long eventTime;

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public ReportLog(String appName, String appVersion, String eventName, Long eventTime) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.eventName = eventName;
        this.eventTime = eventTime;
    }

    public ReportLog() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "ReportLog{" +
                "appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventTime=" + eventTime +
                '}';
    }
}
