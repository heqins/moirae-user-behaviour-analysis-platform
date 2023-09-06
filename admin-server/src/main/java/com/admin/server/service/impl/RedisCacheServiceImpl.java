package com.admin.server.service.impl;

import com.admin.server.helper.RedisHelper;
import com.admin.server.service.ICacheService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service(value = "redisCacheService")
public class RedisCacheServiceImpl implements ICacheService {

    @Resource
    private RedisHelper redisHelper;


}
