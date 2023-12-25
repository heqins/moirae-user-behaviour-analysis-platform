package com.api.common.model.dto.sink;

import cn.hutool.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class EventLogDTO {

    private String appId;

    private String eventName;

    private String dbName;

    private String tableName;

    private Long eventTime;

    private JSONObject jsonObject;

    private String errorReason;

    private String errorHandling;

    private String eventType;

    private Integer status;
    private Map<String, Object> fieldValueMap;

    public EventLogDTO(String appId, String eventName, Long eventTime, JSONObject jsonObject,
                       String errorReason, String errorHandling, String eventType,
                       Integer status, String dbName, String tableName) {
        this.appId = appId;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.jsonObject = jsonObject;
        this.errorReason = errorReason;
        this.errorHandling = errorHandling;
        this.eventType = eventType;
        this.status = status;
        this.tableName = tableName;
        this.dbName = dbName;
    }

    public EventLogDTO() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long createTime) {
        this.eventTime = createTime;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(String errorHandling) {
        this.errorHandling = errorHandling;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Object> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<String, Object> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "appId='" + appId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventTime=" + eventTime +
                ", jsonObject='" + jsonObject + '\'' +
                ", errorReason='" + errorReason + '\'' +
                ", errorHandling='" + errorHandling + '\'' +
                ", eventType='" + eventType + '\'' +
                ", status=" + status +
                '}';
    }
}
