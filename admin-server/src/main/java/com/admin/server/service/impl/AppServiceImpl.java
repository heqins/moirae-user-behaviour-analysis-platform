package com.admin.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.admin.server.dao.AppDao;
import com.admin.server.service.IAppService;
import com.admin.server.util.KeyUtil;
import com.api.common.bo.App;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.CreateAppParam;
import com.api.common.vo.admin.AppPageVo;
import com.api.common.vo.admin.AppVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
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

        if (!StpUtil.isLogin()) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        String user = StpUtil.getLoginIdAsString();
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
        app.setCreateUser(user);

        log.info("appId length:{} key length:{}", app.getAppId().length(), app.getAppKey().length());

        Long id = appDao.createApp(app);
        log.info("create app, id={}", id);
    }

    @Override
    public AppPageVo getAvailableApps(Integer pageNum, Integer pageSize) {
        if (!StpUtil.isLogin()) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        String userId = (String) StpUtil.getLoginId();
        IPage<App> pageResult = appDao.selectPageByUser(userId, pageNum, pageSize);

        AppPageVo appPageVo = new AppPageVo();
        appPageVo.setTotal(pageResult.getTotal());
        appPageVo.setPageSize(pageSize);
        appPageVo.setCurrentNum(pageNum);

        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            appPageVo.setApps(Collections.emptyList());
            return appPageVo;
        }

        List<AppVo> appVoList = AppVo.transferFromAppBo(pageResult.getRecords());
        appPageVo.setApps(appVoList);

        return appPageVo;
    }
}
