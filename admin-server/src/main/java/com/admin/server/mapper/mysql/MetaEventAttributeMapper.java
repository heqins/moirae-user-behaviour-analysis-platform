package com.admin.server.mapper.mysql;

import com.admin.server.model.bo.MetaEventAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetaEventAttributeMapper extends BaseMapper<MetaEventAttribute> {

    void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes);

}
