package com.admin.server.dao;

import com.admin.server.mapper.AppMapper;
import com.admin.server.model.bo.App;
import com.api.common.enums.AppStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AppDao extends ServiceImpl<AppMapper, App> {

    @Resource
    private AppMapper appMapper;

    public Long createApp(App createApp) {
        appMapper.insert(createApp);

        return createApp.getId();
    }

    public List<App> selectByUser(String username) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getCreateUser, username);
        queryWrapper.eq(App::getStatus, AppStatusEnum.ENABLE.getStatus());

        return appMapper.selectList(queryWrapper);
    }

    public IPage<App> selectPageByUser(String username, Integer pageNum, Integer pageSize, String appName, Integer appIsOnline) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        Page<App> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getCreateUser, username);

        if (StringUtils.isNotBlank(appName)) {
            queryWrapper.eq(App::getAppName, appName);
        }

        if (Objects.nonNull(appIsOnline)) {
            queryWrapper.eq(App::getStatus, appIsOnline);
        }
        return appMapper.selectPage(page, queryWrapper);
    }

    public List<App> selectByAppName(String appName) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getAppName, appName);
        queryWrapper.eq(App::getStatus, AppStatusEnum.ENABLE.getStatus());

        return appMapper.selectList(queryWrapper);
    }

    public App selectByAppId(String appId) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getAppId, appId);
        queryWrapper.eq(App::getStatus, AppStatusEnum.ENABLE.getStatus());

        return appMapper.selectOne(queryWrapper);
    }

    public void resetKey(String appId, String appKey) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getAppId, appId);

        App update = new App();
        update.setAppKey(appKey);

        appMapper.update(update, queryWrapper);
    }
}
