package com.api.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
@TableName(value = "app")
public class App {
    @TableId(type = IdType.AUTO)
    private int id;

    private String appName;

    private String describe;

    private String appId;

    private String appKey;

    private int createBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private int updateBy;

    private String appManager;

    private boolean isClose;

    private int saveMonth;

}
