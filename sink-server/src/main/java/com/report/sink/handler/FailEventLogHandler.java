package com.report.sink.handler;

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


}
