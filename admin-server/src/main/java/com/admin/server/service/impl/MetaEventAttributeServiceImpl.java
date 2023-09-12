package com.admin.server.service.impl;

import cn.hutool.core.lang.Pair;
import com.admin.server.dao.MetaEventAttributeDao;
import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.handler.type.DecimalTypeParser;
import com.admin.server.handler.type.DorisTypeParseFactory;
import com.admin.server.handler.type.DorisTypeParser;
import com.admin.server.handler.type.VarcharTypeParser;
import com.admin.server.helper.DorisHelper;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.model.bo.MetaEventAttribute;
import com.api.common.constant.SinkConstants;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.error.ResponseException;
import com.api.common.model.param.admin.UpdateMetaEventAttributeParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMetaEventAttribute(UpdateMetaEventAttributeParam attributeParam) {
        MetaEventAttribute metaEventAttribute = metaEventAttributeDao.selectByEventAndAttributeName(attributeParam.getAppId(), attributeParam.getEventName(), attributeParam.getAttributeName());
        if (metaEventAttribute == null) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_ATTRIBUTE_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_ATTRIBUTE_NOT_EXIST.getMsg());
        }

        String tableName = SinkConstants.generateTableName(attributeParam.getAppId());

        Boolean ifCanChangeColumn = ifCanChangeTableColumn(metaEventAttribute.getDataType(), attributeParam.getDataType(), attributeParam.getLength(), attributeParam.getLimit());
        if (!ifCanChangeColumn) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_ATTRIBUTE_UPDATE_FORBID.getCode(), ErrorCodeEnum.META_EVENT_ATTRIBUTE_UPDATE_FORBID.getMsg());
        }

        String newDataType = generateDataType(attributeParam.getDataType(), attributeParam.getLength(), attributeParam.getLimit());

        dorisHelper.alterTableColumn(dorisDbName, tableName, attributeParam.getAttributeName(), newDataType);

        MetaEventAttribute typeUpdate = new MetaEventAttribute();
        typeUpdate.setDataType(newDataType);

        metaEventAttributeDao.updateAttributeByAppIdAndName(attributeParam.getAppId(), attributeParam.getEventName(),
                attributeParam.getAttributeName(), typeUpdate);
    }

    @Override
    public IPage<MetaEventAttribute> pageQueryByName(String appId, String eventName, String attributeName, Integer pageNum, Integer pageSize) {
        if (StringUtils.isEmpty(appId)) {
            return null;
        }

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        if (pageSize == null || pageSize > 100 || pageSize <= 0) {
            pageSize = 10;
        }

        return metaEventAttributeDao.pageQueryByName(appId, eventName, attributeName, pageNum, pageSize);
    }

    /**
     *
     * @param oldColumnType
     * @param dataType
     * @param length 如果是varchar则代表长度，如果是decimal则代表整数位
     * @param limit decimal，代表小数位
     * @return
     */
    private Boolean ifCanChangeTableColumn(String oldColumnType, String dataType, Integer length, Integer limit) {
        if (oldColumnType == null || dataType == null) {
            return false;
        }

        if (!oldColumnType.startsWith(dataType)) {
            return false;
        }

        DorisTypeParseFactory factory = new DorisTypeParseFactory();

        // 如果是varchar，则pair第一个数代表长度
        if (oldColumnType.startsWith(AttributeDataTypeEnum.VARCHAR.getDorisType())) {
            DorisTypeParser dorisTypeParser = factory.getDorisTypeParser(VarcharTypeParser.class);

            Pair<Integer, Integer> oldPair = dorisTypeParser.parseType(oldColumnType);

            if (oldPair == null || length == null) {
                return false;
            }

            return length >= oldPair.getKey();
        }

        // 如果是decimal，则pair第一个数代表整数位，第二个数代表小数位
        if (oldColumnType.startsWith(AttributeDataTypeEnum.DECIMAL.getDorisType())) {
            DorisTypeParser dorisTypeParser = factory.getDorisTypeParser(DecimalTypeParser.class);

            Pair<Integer, Integer> oldPair = dorisTypeParser.parseType(oldColumnType);

            if (oldPair == null || length == null || limit == null) {
                return false;
            }

            return length >= oldPair.getKey() && limit >= oldPair.getValue();
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
