package com.report.sink.service.impl;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.api.common.constant.RedisCacheConstants;
import com.api.common.model.dto.admin.AppDTO;
import com.api.common.model.dto.sink.MetaEventAttributeDTO;
import com.api.common.model.dto.sink.TableColumnDTO;
import com.report.sink.helper.RedisHelper;
import com.report.sink.model.bo.MetaEvent;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Service(value = "redisCacheService")
@Slf4j
public class RedisCacheServiceImpl implements ICacheService {

    @Resource
    private RedisHelper redisHelper;

    @Override
    public List<TableColumnDTO> getColumnCache(String dbName, String tableName) {
        String cacheKey = RedisCacheConstants.getDorisColumnCacheKey(dbName, tableName);
        List<String> hashValues = redisHelper.getHashValues(cacheKey);
        if (CollectionUtils.isEmpty(hashValues)) {
            return null;
        }

        List<TableColumnDTO> columns = hashValues.stream()
                .map(value -> JSONUtil.toBean(value, TableColumnDTO.class))
                .collect(Collectors.toList());
        return columns;
    }

    @Override
    public void setColumnCache(String dbName, String tableName, List<TableColumnDTO> columns) {
        String cacheKey = RedisCacheConstants.getDorisColumnCacheKey(dbName, tableName);
        columns.forEach(column -> {
            String hashKey = column.getColumnName();
            redisHelper.putHashValue(cacheKey, hashKey, JSONUtil.toJsonStr(column));
        });
    }

    @Override
    public void removeColumnCache(String dbName, String tableName) {
        String cacheKey = RedisCacheConstants.getDorisColumnCacheKey(dbName, tableName);
        redisHelper.deleteKey(cacheKey);
    }

    @Override
    public List<MetaEvent> getMetaEventsCache(String appId) {
        return null;
    }

    @Override
    public MetaEvent getMetaEventCache(String appId, String eventName) {
        String cacheKey = RedisCacheConstants.getMetaEventCacheKey(appId, eventName);

        String value = redisHelper.getValue(cacheKey);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return JSONUtil.toBean(value, MetaEvent.class);
    }

    @Override
    public void setMetaEventCache(String appId, String eventName, MetaEvent metaEvent) {
        String cacheKey = RedisCacheConstants.getMetaEventCacheKey(appId, eventName);
        redisHelper.setIfAbsent(cacheKey, JSONUtil.toJsonStr(metaEvent));
    }

    @Override
    public AppDTO getAppInfoCache(String appId) {
        String cacheKey = RedisCacheConstants.getAppCacheKey(appId);
        String value = redisHelper.getValue(cacheKey);
        if (value == null) {
            return null;
        }

        boolean typeJSON = JSONUtil.isTypeJSON(value);

        return JSONUtil.toBean(value, AppDTO.class);
    }

    @Override
    public Integer getMetaEventStatusCache(String appId, String eventName) {
        String cacheKey = RedisCacheConstants.getMetaEventCacheKey(appId, eventName);
        String value = redisHelper.getValue(cacheKey);
        if (!NumberUtil.isNumber(value)) {
            return null;
        }

        return Integer.parseInt(value);
    }

    @Override
    public List<MetaEventAttributeDTO> getMetaEventAttributeCache(String appId, String eventName) {
        return null;
    }

    @Override
    public List<MetaEventAttributeDTO> multiGetMetaEventAttributeCache(List<String> keys) {
        List<String> values = redisHelper.multiGet(keys);
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }

        return values.stream().filter(Objects::nonNull).map(value -> JSONUtil.toBean(value, MetaEventAttributeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void setAppInfoCache(String appId, AppDTO appDTO) {
        String cacheKey = RedisCacheConstants.getAppCacheKey(appId);
        redisHelper.setValue(cacheKey, JSONUtil.toJsonStr(appDTO));
    }
}
