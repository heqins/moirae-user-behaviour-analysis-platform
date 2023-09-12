package com.report.sink.model.bo;

import com.api.common.model.param.admin.CreateAppParam;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@TableName(value = "app")
public class App {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appName;

    private String description;

    private String appId;

    private String appKey;

    private String createUser;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSaveMonth() {
        return saveMonth;
    }

    public void setSaveMonth(Integer saveMonth) {
        this.saveMonth = saveMonth;
    }
}
