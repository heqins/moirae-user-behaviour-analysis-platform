package com.api.common.vo.admin;

import lombok.Data;

@Data
public class AppVo {

    private String appId;

    private String appName;

    private String description;

    private Integer closed;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private Integer id;

}
