package com.report.sink.service;

import com.api.common.model.dto.admin.AppDTO;
import com.api.common.model.dto.sink.MetaEventAttributeDTO;
import com.api.common.model.dto.sink.TableColumnDTO;
import com.report.sink.model.bo.MetaEvent;

import java.util.List;

/**
 * @author heqin
 */
public interface ICacheService {

    List<TableColumnDTO> getColumnCache(String dbName, String tableName);

    void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns);

    void removeColumnCache(String dbName, String tableName);

    List<MetaEvent> getMetaEventsCache(String appId);

    MetaEvent getMetaEventCache(String appId, String eventName);

    void setMetaEventCache(String appId, String eventName, MetaEvent metaEvent);

    AppDTO getAppInfoCache(String appId);

    Integer getMetaEventStatusCache(String appId, String eventName);

    List<MetaEventAttributeDTO> getMetaEventAttributeCache(String appId, String eventName);

    List<MetaEventAttributeDTO> multiGetMetaEventAttributeCache(List<String> keys);

    void setAppInfoCache(String appId, AppDTO appDTO);

}
