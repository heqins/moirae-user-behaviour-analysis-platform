package com.admin.server.mapper;

import com.api.common.bo.Attribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttributeMapper extends BaseMapper<Attribute> {

    void batchInsertAttributes(List<Attribute> attributes);

}
