package com.admin.server.service.impl;

import com.admin.server.dao.AppDao;
import com.admin.server.service.IAppService;
import com.admin.server.util.KeyUtil;
import com.api.common.bo.App;
import com.api.common.param.admin.CreateAppParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class AppServiceImpl implements IAppService {

    @Resource
    private AppDao appDao;

    @Override
    public void createApp(CreateAppParam createAppParam) {
        if (Objects.isNull(createAppParam)) {
            return;
        }

        App app = App.transferFromCreateAppParam(createAppParam);
        if (Objects.isNull(app)) {
            return;
        }

        UUID uuid = KeyUtil.generateAppId();
        app.setAppId(uuid.toString());

        String appKey = null;
        try {
            appKey = KeyUtil.generateAppKey();
        }catch (NoSuchAlgorithmException e) {
            log.error("AppServiceImpl createApp NoSuchAlgorithmException", e);
        }

        if (appKey == null) {
            return;
        }

        app.setAppKey(appKey);

        log.info("appId length:{} key length:{}", app.getAppId().length(), app.getAppKey().length());

        Long id = appDao.createApp(app);
        log.info("create app, id={}", id);
    }
}
