package com.api.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class MetaEventAttribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventName;

    private String appId;

    private String eventAttribute;

}
