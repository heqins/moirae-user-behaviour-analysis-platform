package com.report.sink.service;

import com.api.common.dto.AppDTO;
import com.api.common.entity.App;
import com.report.sink.dao.AppDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AppServiceImpl implements IAppService{

    @Resource
    private AppDao appDao;

    @Override
    public void generateApp(String appName, String describe) {

    }

    @Override
    public AppDTO getAppInfo(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }

        App app = appDao.getAppById(appId);
        if (app == null) {
            return null;
        }

        return null;
    }
}
