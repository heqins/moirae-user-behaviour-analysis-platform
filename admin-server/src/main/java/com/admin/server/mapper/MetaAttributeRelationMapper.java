package com.admin.server.mapper;

import com.api.common.bo.MetaAttributeRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetaAttributeRelationMapper extends BaseMapper<MetaAttributeRelation> {

    void batchInsertAttributes(List<MetaAttributeRelation> attributes);

}
