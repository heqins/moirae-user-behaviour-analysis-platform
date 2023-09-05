package com.api.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class MetaEvent {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventName;

    private String appId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer yesterdayCount;

    private String showName;
}
