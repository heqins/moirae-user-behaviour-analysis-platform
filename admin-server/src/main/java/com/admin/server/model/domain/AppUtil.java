package com.admin.server.model.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.admin.server.model.bo.App;
import com.api.common.model.param.admin.CreateAppParam;
import com.api.common.model.vo.admin.AppVo;
import com.api.common.util.DateTimeUtil;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AppUtil {

    public static App transferFromCreateAppParam(CreateAppParam appParam) {
        if (appParam == null || appParam.getAppName() == null) {
            return null;
        }

        App app = new App();
        BeanUtils.copyProperties(appParam, app);
        app.setAppName(appParam.getAppName());

        return app;
    }

    public static List<AppVo> transferFromAppBo(List<App> appList) {
        if (CollectionUtil.isEmpty(appList)) {
            return Collections.emptyList();
        }

        return appList.stream().map(app -> {
            AppVo appVo = new AppVo();
            BeanUtil.copyProperties(app, appVo);

            appVo.setCreateTime(DateTimeUtil.toEpoch(app.getCreateTime()));
            appVo.setUpdateTime(DateTimeUtil.toEpoch(app.getUpdateTime()));

            return appVo;
        }).collect(Collectors.toList());
    }
}
