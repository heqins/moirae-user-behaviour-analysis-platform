package com.admin.server.service.impl;

import cn.hutool.core.lang.Pair;
import com.admin.server.dao.MetaEventAttributeDao;
import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.helper.DorisHelper;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.util.TypeUtil;
import com.api.common.bo.MetaEventAttribute;
import com.api.common.constant.ConfigConstant;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.UpdateMetaEventAttributeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author heqin
 */
@Service
@Slf4j
public class MetaEventAttributeServiceImpl implements IMetaEventAttributeService {

    @Value("${spring.datasource.doris.db-name}")
    private String dorisDbName;

    @Resource
    private MetaEventAttributeDao metaEventAttributeDao;

    @Resource
    private DorisHelper dorisHelper;

    @Override
    public List<MetaEventAttribute> queryByName(List<String> attributeNames, String appId) {
        if (StringUtils.isEmpty(appId) || CollectionUtils.isEmpty(attributeNames)) {
            return Collections.emptyList();
        }

        return metaEventAttributeDao.selectByName(appId, attributeNames);
    }

    @Override
    public void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes) {
        metaEventAttributeDao.batchInsertAttributes(metaEventAttributes);
    }

    @Override
    public void updateMetaEventAttribute(UpdateMetaEventAttributeParam attributeParam) {
        MetaEventAttribute metaEventAttribute = metaEventAttributeDao.selectByEventAndAttributeName(attributeParam.getAppId(), attributeParam.getEventName(), attributeParam.getAttributeName());
        if (metaEventAttribute == null) {
            // todo:
            throw new ResponseException(ErrorCodeEnum.META_EVENT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_EXIST.getMsg());
        }

        String tableName = ConfigConstant.generateTableName(attributeParam.getAppId());

        Boolean ifCanChangeColumn = ifCanChangeTableColumn(metaEventAttribute.getDataType(), attributeParam.getDataType(), attributeParam.getLength(), attributeParam.getLimit());

        if (ifCanChangeColumn) {

            String newDataType = generateDataType(attributeParam.getDataType(), attributeParam.getLength(), attributeParam.getLimit());

            dorisHelper.alterTableColumn(dorisDbName, tableName, attributeParam.getAttributeName(), newDataType);

            metaEventAttributeDao.updateAttributeByAppIdAndName(attributeParam.getAppId(),
                    attributeParam.getEventName(), attributeParam.getAttributeName());

        }
    }

    private Boolean ifCanChangeTableColumn(String oldColumnType, String dataType, Integer length, Integer limit) {
        if (oldColumnType.startsWith(AttributeDataTypeEnum.VARCHAR.getDorisType())) {
            Pair<Integer, Integer> newPair = TypeUtil.parseTypeNumber(TypeUtil.VARCHAR_PATTERN, dataType);
            Pair<Integer, Integer> oldPair = TypeUtil.parseTypeNumber(TypeUtil.VARCHAR_PATTERN, oldColumnType);

            if (newPair == null || oldPair == null) {
                return false;
            }

            return newPair.getKey() >= oldPair.getKey();
        }

        if (oldColumnType.startsWith(AttributeDataTypeEnum.DECIMAL.getDorisType())) {
            Pair<Integer, Integer> newPair = TypeUtil.parseTypeNumber(TypeUtil.DECIMAL_PATTERN, dataType);
            Pair<Integer, Integer> oldPair = TypeUtil.parseTypeNumber(TypeUtil.DECIMAL_PATTERN, oldColumnType);

            if (newPair == null || oldPair == null) {
                return false;
            }

            return newPair.getKey() >= oldPair.getKey() && newPair.getValue() >= oldPair.getValue();
        }

        return false;
    }

    private String generateDataType(String dataType, Integer length, Integer limit) {
        if (StringUtils.isEmpty(dataType)) {
            return null;
        }

        return AttributeDataTypeEnum.generateDorisTypeWithLength(dataType, length, limit);
    }
}
