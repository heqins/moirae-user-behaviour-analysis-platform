package com.api.common.bo;

import com.api.common.param.admin.CreateAppParam;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
@TableName(value = "app")
public class App {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String appName;

    private String description;

    private String appId;

    private String appKey;

    private Integer createBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer updateBy;

    private String appManager;

    private Boolean closed;

    private Integer saveMonth;

    public static App transferFromCreateAppParam(CreateAppParam appParam) {
        if (appParam == null || appParam.getAppName() == null) {
            return null;
        }

        App app = new App();
        BeanUtils.copyProperties(appParam, app);
        app.setAppName(appParam.getAppName());

        return app;
    }
}
