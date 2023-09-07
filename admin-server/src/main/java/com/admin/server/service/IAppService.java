package com.admin.server.service;

import com.api.common.param.admin.CreateAppParam;
import com.api.common.vo.PageVo;
import com.api.common.vo.admin.AppPageVo;

public interface IAppService {

    void createApp(CreateAppParam createAppParam);

    PageVo<AppPageVo> getAvailableApps(Integer pageNum, Integer pageSize);

}
