package com.admin.server.handler.analysis;

import com.admin.server.helper.DorisHelper;
import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Resource
    private DorisHelper dorisHelper;

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        Pair<String, List<String>> sqlPair = getEventSql(param);

        List<Map<String, Object>> maps = dorisHelper.selectEventAnalysis(sqlPair);

        return null;
    }

    private Pair<String, List<String>> getEventSql(AnalysisParam param) {
        if (!param.isValid()) {
            throw new IllegalStateException("参数错误");
        }

        Pair<String, List<String>> whereSqlPair = SqlUtil.getWhereSql(param.getWhereFilter());
        List<String> sqlArgs = whereSqlPair.getValue();

        Pair<String, List<String>> dateRangeSqlPair = SqlUtil.getDateRangeSql(param.getDateRange());
        sqlArgs.addAll(dateRangeSqlPair.getValue());

        String sql = whereSqlPair.getKey() + dateRangeSqlPair.getKey();

        Pair<String, List<String>> sqlPair = SqlUtil.getAggregation(0, param, sqlArgs, sql);

        // order by
        return sqlPair;
    }
}
