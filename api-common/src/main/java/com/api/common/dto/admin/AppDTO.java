package com.api.common.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class AppDTO {

    private String appName;

    private String description;

    private String appId;

    private String appKey;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private Integer saveMonth;
}
