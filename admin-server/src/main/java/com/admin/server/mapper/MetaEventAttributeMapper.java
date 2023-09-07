package com.admin.server.mapper;

import com.api.common.bo.MetaEventAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MetaEventAttributeMapper extends BaseMapper<MetaEventAttribute> {

    void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes);

}
