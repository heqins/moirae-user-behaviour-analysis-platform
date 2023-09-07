package com.admin.server.dao;

import com.admin.server.mapper.AppMapper;
import com.api.common.bo.App;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
        queryWrapper.eq(App::getStatus, 1);

        return appMapper.selectList(queryWrapper);
    }

    public IPage<App> selectPageByUser(String username, Integer pageNum, Integer pageSize) {
        Page<App> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(App::getCreateUser, username);
        queryWrapper.eq(App::getStatus, 0);

        return appMapper.selectPage(page, queryWrapper);
    }
}
