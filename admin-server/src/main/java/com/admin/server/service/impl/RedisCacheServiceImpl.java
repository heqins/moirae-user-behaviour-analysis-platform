package com.admin.server.service.impl;

import com.admin.server.helper.RedisHelper;
import com.admin.server.service.ICacheService;
import com.api.common.constant.RedisCacheConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service(value = "redisCacheService")
public class RedisCacheServiceImpl implements ICacheService {

    @Resource
    private RedisHelper redisHelper;

    @Override
    public void setMetaEventCache(String appId, String eventName, Integer status) {
        String cacheKey = RedisCacheConstants.getMetaEventCacheKey(appId, eventName);

        redisHelper.setValue(cacheKey, String.valueOf(status));
    }
}
