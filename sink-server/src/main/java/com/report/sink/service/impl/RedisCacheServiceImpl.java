package com.report.sink.service.impl;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.api.common.constant.RedisCacheConstants;
import com.api.common.dto.admin.AppDTO;
import com.api.common.dto.sink.MetaEventAttributeDTO;
import com.api.common.dto.sink.TableColumnDTO;
import com.api.common.bo.MetaEvent;
import com.report.sink.helper.RedisHelper;
import com.report.sink.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        return null;
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
        return null;
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
}
