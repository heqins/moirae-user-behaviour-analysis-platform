package com.api.common.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author heqin
 */
@Data
public class MetaAttributeRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventName;

    private String appId;

    private String eventAttribute;

}
