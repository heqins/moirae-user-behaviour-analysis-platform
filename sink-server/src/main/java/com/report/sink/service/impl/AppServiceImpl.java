package com.report.sink.service.impl;

import com.api.common.model.dto.admin.AppDTO;
import com.api.common.bo.App;
import com.report.sink.dao.AppDao;
import com.report.sink.service.IAppService;
import com.report.sink.service.ICacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AppServiceImpl implements IAppService {

    @Resource
    private AppDao appDao;

    @Resource(name = "redisCacheService")
    private ICacheService redisCacheService;

    @Override
    public AppDTO getAppInfo(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }

        AppDTO appInfoCache = redisCacheService.getAppInfoCache(appId);
        if (Objects.nonNull(appInfoCache)) {
            return appInfoCache;
        }

        App app = appDao.getAppById(appId);
        if (app == null) {
            return null;
        }

        AppDTO appInfo = new AppDTO();
        BeanUtils.copyProperties(app, appInfo);

        return appInfo;
    }
}
