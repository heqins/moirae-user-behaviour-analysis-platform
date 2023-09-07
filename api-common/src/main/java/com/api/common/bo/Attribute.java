package com.api.common.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class Attribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String attributeName;

    private String dataType;

    private String showName;

    private Integer attributeType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String appId;

    private Integer status;

}
