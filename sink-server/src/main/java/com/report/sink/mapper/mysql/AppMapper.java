package com.report.sink.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.report.sink.model.bo.App;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMapper extends BaseMapper<App> {

}
