package com.api.common.vo.admin;

import lombok.Data;

/**
 * @author heqin
 */
@Data
public class MetaEventAttributesVo {

    private Long id;

    private String dataTypeName;

    private String dataType;

    private String attributeName;

    private Integer attributeSource;

    private Integer attributeType;

    private Integer status;

    private String appId;

    private Long createTime;

    private Long updateTime;


}
