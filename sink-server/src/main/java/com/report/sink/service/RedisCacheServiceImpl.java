package com.report.sink.service;


import com.report.sink.helper.RedisHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RedisCacheServiceImpl implements ICacheService{

    @Resource
    private RedisHelper redisHelper;

    @Override
    public List<String> getDimCache(String appId) {
        return null;
    }
}
