package com.api.common.model.vo.admin;

import lombok.Data;

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

}
