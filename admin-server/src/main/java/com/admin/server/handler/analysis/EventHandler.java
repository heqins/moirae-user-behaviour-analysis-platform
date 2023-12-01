package com.admin.server.handler.analysis;

import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EventHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private final AnalysisParam param;

    public EventHandler(AnalysisParam param) {
        this.param = param;
    }

    @Override
    public void execute(AnalysisParam param) {
        String sql = getEventSql(param);

    }

    private String getEventSql(AnalysisParam param) {
        Pair<String, List<String>> whereSqlPair = SqlUtil.getWhereSql(param.getWhereFilter());
        List<String> sqlArgs = whereSqlPair.getValue();

        Pair<String, List<String>> dateRangeSqlPair = SqlUtil.getDateRangeSql(param.getDateRange());
        sqlArgs.addAll(dateRangeSqlPair.getValue());

        String sql = whereSqlPair.getKey() + dateRangeSqlPair.getKey();

        for (int i = 0; i < param.getAggregations().size(); i++) {
            Pair<String, List<String>> sqlPair = SqlUtil.getAggregation(i, param, sqlArgs, sql);

        }

        // order by
        return sql;
    }
}
