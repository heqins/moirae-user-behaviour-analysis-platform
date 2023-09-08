package com.api.common.model.vo.admin;

import lombok.Data;

/**
 * @author heqin
 */
@Data
public class MetaEventAttributeVo {

    private Long id;

    private String dataTypeName;

    private String dataType;

    private String attributeName;

    private Integer attributeType;

    private Integer status;

    private String appId;

    private Long createTime;

    private Long updateTime;

}
