package com.report.sink.dao;

import com.api.common.bo.App;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.report.sink.mapper.mysql.AppMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AppDao extends ServiceImpl<AppMapper, App> {

    @Resource
    private AppMapper appMapper;

    public App getAppById(String appId) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getAppId, appId);

        return appMapper.selectOne(queryWrapper);
    }
}
