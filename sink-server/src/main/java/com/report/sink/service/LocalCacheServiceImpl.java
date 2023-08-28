package com.report.sink.service;

import com.api.common.dto.TableColumnDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.report.sink.constants.CacheConstants;
import com.report.sink.helper.DorisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class LocalCacheServiceImpl implements ICacheService{

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
}
