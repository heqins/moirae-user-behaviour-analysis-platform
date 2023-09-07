package com.api.common.vo.admin;

import com.api.common.bo.App;
import com.api.common.util.DateTimeUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AppVo {

    private String appId;

    private String appName;

    private String description;

    private Integer status;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private Long id;

    public static List<AppVo> transferFromAppBo(List<App> appList) {
        if (CollectionUtils.isEmpty(appList)) {
            return Collections.emptyList();
        }

        return appList.stream().map(app -> {
            AppVo appVo = new AppVo();
            BeanUtils.copyProperties(app, appVo);

            appVo.setCreateTime(DateTimeUtil.toEpoch(app.getCreateTime()));
            appVo.setUpdateTime(DateTimeUtil.toEpoch(app.getUpdateTime()));

            return appVo;
        }).collect(Collectors.toList());
    }
}
