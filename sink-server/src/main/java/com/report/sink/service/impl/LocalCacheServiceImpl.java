package com.report.sink.service.impl;

import com.api.common.model.dto.admin.AppDTO;
import com.api.common.model.dto.sink.MetaEventAttributeDTO;
import com.api.common.model.dto.sink.TableColumnDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.api.common.constant.LocalCacheConstants;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class LocalCacheServiceImpl implements ICacheService {

    @Resource(name = "columnLocalCache")
    private Cache<String, List<TableColumnDTO>> columnLocalCache;

    @Override
    public List<TableColumnDTO> getColumnCache(String dbName, String tableName) {
        String columnLocalCacheKey = LocalCacheConstants.getColumnLocalCacheKey(dbName, tableName);
        return columnLocalCache.getIfPresent(columnLocalCacheKey);
    }

    @Override
    public void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns) {

    }

    @Override
    public void removeColumnCache(String dbName, String tableName) {
    }

    @Override
    public List<MetaEvent> getMetaEventsCache(String appId) {
        return null;
    }

    @Override
    public MetaEvent getMetaEventCache(String appId, String eventName) {
        return null;
    }

    @Override
    public void setMetaEventCache(String appId, String eventName, MetaEvent metaEvent) {

    }

    @Override
    public AppDTO getAppInfoCache(String appId) {
        return null;
    }

    @Override
    public Integer getMetaEventStatusCache(String appId, String eventName) {
        return null;
    }

    @Override
    public List<MetaEventAttributeDTO> getMetaEventAttributeCache(String appId, String eventName) {
        return null;
    }

    @Override
    public List<MetaEventAttributeDTO> multiGetMetaEventAttributeCache(List<String> keys) {
        return null;
    }

    @Override
    public void setAppInfoCache(String appId, AppDTO appDTO) {

    }
}
