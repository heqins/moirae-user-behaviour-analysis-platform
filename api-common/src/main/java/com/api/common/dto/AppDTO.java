package com.api.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class AppDTO {

    private String appName;

    private String describe;

    private String appId;

    private String appKey;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String appManager;

    private boolean isClose;

    private int saveMonth;
}
