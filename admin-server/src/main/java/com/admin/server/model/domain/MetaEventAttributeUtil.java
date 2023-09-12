package com.admin.server.model.domain;

import com.admin.server.model.bo.MetaEventAttribute;
import com.admin.server.model.dto.DbColumnValueDto;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.model.vo.admin.EventAttributePropVo;
import com.api.common.model.vo.admin.EventAttributeValuePageVo;
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

    public static List<EventAttributePropVo> transferToPropVoFromAttributeBo(List<MetaEventAttribute> attributes) {
        if (CollectionUtils.isEmpty(attributes)) {
            return Collections.emptyList();
        }

        return attributes.stream().map(attribute  -> {
            EventAttributePropVo propVO = new EventAttributePropVo();
            BeanUtils.copyProperties(attribute, propVO);

            propVO.setDisplayName(attribute.getShowName());

            String typeDescription = AttributeDataTypeEnum.getDataTypeDescription(attribute.getDataType());
            propVO.setDataTypeName(typeDescription);

            return propVO;
        }).collect(Collectors.toList());
    }

    public static EventAttributeValuePageVo transferToValuePageFromColumnValueDto(String appId, String eventName, String attributeName, List<DbColumnValueDto> columnValues) {
        EventAttributeValuePageVo page = new EventAttributeValuePageVo();
        if (CollectionUtils.isEmpty(columnValues)) {
            return page;
        }

        List<String> values = columnValues.stream().map(DbColumnValueDto::getValue).collect(Collectors.toList());
        page.setValues(values);
        page.setAttributeName(attributeName);
        page.setEventName(eventName);
        page.setAppId(appId);

        return page;
    }
}
