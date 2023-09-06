package com.report.sink.service.impl;

import com.api.common.dto.admin.AppDTO;
import com.api.common.dto.sink.TableColumnDTO;
import com.api.common.bo.MetaEvent;
import com.github.benmanes.caffeine.cache.Cache;
import com.report.sink.constants.CacheConstants;
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
        String columnLocalCacheKey = CacheConstants.getColumnLocalCacheKey(dbName, tableName);
        return columnLocalCache.getIfPresent(columnLocalCacheKey);
    }

    @Override
    public void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns) {

    }

    @Override
    public void removeColumnCache(String dbName, String tableName) {

    }

    @Override
    public List<MetaEvent> getMetaEventCache(String appId) {
        return null;
    }

    @Override
    public AppDTO getAppInfoCache(String appId) {
        return null;
    }
}
