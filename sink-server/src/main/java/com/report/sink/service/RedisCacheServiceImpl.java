package com.report.sink.service;


import cn.hutool.json.JSONUtil;
import com.api.common.dto.TableFieldDTO;
import com.report.sink.constants.CacheConstants;
import com.report.sink.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author heqin
 */
@Service
@Slf4j
public class RedisCacheServiceImpl implements ICacheService{

    @Resource
    private RedisHelper redisHelper;

    @Override
    public Map<String, String> getAppFieldsCache(String appId) {
        return null;
    }

    @Override
    public List<String> getAppRulesCache(String appId) {
        return null;
    }

    @Override
    public void setAppFieldCache(String appId, TableFieldDTO tableFieldDTO) {
        if (StringUtils.isBlank(appId) || Objects.isNull(tableFieldDTO)) {
            log.error("RedisCacheServiceImpl setAppFieldCache param null appId:{} tableFieldDto:{}", appId, JSONUtil.toJsonStr(tableFieldDTO));
            return;
        }

        try {
            redisHelper.putIfAbsentHashValue(CacheConstants.getAppFieldCacheKey(appId), tableFieldDTO.getFieldName(), JSONUtil.toJsonStr(tableFieldDTO));
        }catch (IllegalArgumentException e) {
            log.error("RedisCacheServiceImpl setAppFieldCache argument error", e);
        }
    }
}
