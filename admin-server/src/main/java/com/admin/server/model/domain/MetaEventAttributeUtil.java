package com.admin.server.model.domain;

import com.admin.server.model.bo.MetaEventAttribute;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.model.vo.admin.MetaEventAttributeVo;
import com.api.common.util.DateTimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MetaEventAttributeUtil {


    public static List<MetaEventAttributeVo> transferFromAttributeBo(List<MetaEventAttribute> metaEventAttributes) {
        if (CollectionUtils.isEmpty(metaEventAttributes)) {
            return Collections.emptyList();
        }

        return metaEventAttributes.stream().map(attribute -> {
            MetaEventAttributeVo vo = new MetaEventAttributeVo();
            BeanUtils.copyProperties(attribute, vo);

            vo.setCreateTime(DateTimeUtil.toEpoch(attribute.getCreateTime()));
            vo.setUpdateTime(DateTimeUtil.toEpoch(attribute.getUpdateTime()));

            String dataTypeName = AttributeDataTypeEnum.getDataTypeDescription(attribute.getDataType());
            vo.setDataTypeName(dataTypeName);

            return vo;
        }).collect(Collectors.toList());
    }
}
