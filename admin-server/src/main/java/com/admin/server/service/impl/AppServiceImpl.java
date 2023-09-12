package com.admin.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.admin.server.dao.AppDao;
import com.admin.server.helper.DorisHelper;
import com.admin.server.model.domain.AppUtil;
import com.admin.server.service.IAppService;
import com.admin.server.utils.KeyUtil;
import com.admin.server.utils.MyPageUtil;
import com.admin.server.model.bo.App;
import com.api.common.constant.ConfigConstant;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.model.param.admin.CreateAppParam;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.AppPageVo;
import com.api.common.model.vo.admin.AppVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AppServiceImpl implements IAppService {

    private final Logger logger = LoggerFactory.getLogger(DorisHelper.class);

    @Resource
    private AppDao appDao;

    @Resource
    private DorisHelper dorisHelper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createApp(CreateAppParam createAppParam) {
        if (Objects.isNull(createAppParam)) {
            return;
        }

        if (!StpUtil.isLogin()) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        String user = StpUtil.getLoginIdAsString();
        App app = AppUtil.transferFromCreateAppParam(createAppParam);

        // todo:
        if (Objects.isNull(app)) {
            return;
        }

        String generateAppId = KeyUtil.generateAppId();

        app.setAppId(generateAppId);
        String appKey = null;
        try {
            appKey = KeyUtil.generateAppKey();
        }catch (NoSuchAlgorithmException e) {
            logger.error("createApp NoSuchAlgorithmException", e);
        }

        if (appKey == null) {
            throw new IllegalStateException("createApp appKey is null");
        }

        app.setAppKey(appKey);
        app.setCreateUser(user);

        Long id = appDao.createApp(app);
        logger.info("createApp id={}", id);

        String tableName = ConfigConstant.generateTableName(generateAppId);
        dorisHelper.createApp("user_behaviour_analysis", tableName);
    }

    @Override
    public PageVo<AppPageVo> getAvailableApps(Integer pageNum, Integer pageSize) {
        if (!StpUtil.isLogin()) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        String userId = (String) StpUtil.getLoginId();
        IPage<App> pageResult = appDao.selectPageByUser(userId, pageNum, pageSize);

        AppPageVo appPageVo = new AppPageVo();
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            appPageVo.setApps(Collections.emptyList());
            return MyPageUtil.constructPageVo(pageNum, pageSize, pageResult.getTotal(), appPageVo);
        }

        List<AppVo> appVoList = AppUtil.transferFromAppBo(pageResult.getRecords());
        appPageVo.setApps(appVoList);

        return MyPageUtil.constructPageVo(pageNum, pageSize, pageResult.getTotal(), appPageVo);
    }

    @Override
    public App getByAppID(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return null;
        }

        return appDao.selectByAppId(appId);
    }
}
