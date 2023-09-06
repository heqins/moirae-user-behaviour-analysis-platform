package com.report.sink.dao;

import com.api.common.bo.App;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.report.sink.mapper.AppMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AppDao {

    @Resource
    private AppMapper appMapper;

    public App getAppById(String appId) {
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getAppId, appId);

        return appMapper.selectOne(queryWrapper);
    }
}
