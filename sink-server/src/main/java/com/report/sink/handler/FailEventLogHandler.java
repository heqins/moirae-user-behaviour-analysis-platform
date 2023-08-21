package com.report.sink.handler;

import cn.hutool.json.JSONObject;
import com.report.sink.properties.DataSourceProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@Component
public class FailEventLogHandler {

    @Resource
    private DataSourceProperty dataSourceProperty;

    public void addEvent(JSONObject jsonObject) {

    }
}
