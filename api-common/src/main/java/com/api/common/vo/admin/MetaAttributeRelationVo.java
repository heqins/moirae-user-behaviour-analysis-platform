package com.api.common.vo.admin;

import com.api.common.bo.Attribute;
import com.api.common.util.DateTimeUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heqin
 */
@Data
public class MetaAttributeRelationVo {

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


    public static List<MetaAttributeRelationVo> transferFromAttributeBo(List<Attribute> attributes) {
        if (CollectionUtils.isEmpty(attributes)) {
            return Collections.emptyList();
        }

        return attributes.stream().map(attribute -> {
            MetaAttributeRelationVo vo = new MetaAttributeRelationVo();
            BeanUtils.copyProperties(attribute, vo);

            vo.setCreateTime(DateTimeUtil.toEpoch(attribute.getCreateTime()));
            vo.setUpdateTime(DateTimeUtil.toEpoch(attribute.getUpdateTime()));

            return vo;
        }).collect(Collectors.toList());
    }
}
