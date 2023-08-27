package com.api.common.dto;

import cn.hutool.json.JSONObject;

public class LogEventDTO {

    private JSONObject dataObject;

    private String dbName;

    private String tableName;

    public LogEventDTO(JSONObject dataObject, String dbName, String tableName) {
        this.dataObject = dataObject;
        this.dbName = dbName;
        this.tableName = tableName;
    }

    public LogEventDTO() {
    }


    public JSONObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(JSONObject dataObject) {
        this.dataObject = dataObject;
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
}
