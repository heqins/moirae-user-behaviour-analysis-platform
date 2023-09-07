package com.report.sink.service;

import com.api.common.dto.admin.AppDTO;
import com.api.common.dto.sink.TableColumnDTO;
import com.api.common.bo.MetaEvent;

import java.util.List;

/**
 * @author heqin
 */
public interface ICacheService {

    List<TableColumnDTO> getColumnCache(String dbName, String tableName);

    void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns);

    void removeColumnCache(String dbName, String tableName);

    List<MetaEvent> getMetaEventCache(String appId);

    AppDTO getAppInfoCache(String appId);

    Integer getMetaEventStatusCache(String appId, String eventName);

}
