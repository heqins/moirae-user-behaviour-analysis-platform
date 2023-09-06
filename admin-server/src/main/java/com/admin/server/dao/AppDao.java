package com.admin.server.dao;

import com.admin.server.mapper.AppMapper;
import com.api.common.bo.App;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AppDao {

    @Resource
    private AppMapper appMapper;

    public Long createApp(App createApp) {
        appMapper.insert(createApp);

        return createApp.getId();
    }
}
