package com.api.common.vo.admin;

import com.api.common.bo.MetaEventAttribute;
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


    public static List<MetaAttributeRelationVo> transferFromAttributeBo(List<MetaEventAttribute> metaEventAttributes) {
        if (CollectionUtils.isEmpty(metaEventAttributes)) {
            return Collections.emptyList();
        }

        return metaEventAttributes.stream().map(attribute -> {
            MetaAttributeRelationVo vo = new MetaAttributeRelationVo();
            BeanUtils.copyProperties(attribute, vo);

            vo.setCreateTime(DateTimeUtil.toEpoch(attribute.getCreateTime()));
            vo.setUpdateTime(DateTimeUtil.toEpoch(attribute.getUpdateTime()));

            return vo;
        }).collect(Collectors.toList());
    }
}
