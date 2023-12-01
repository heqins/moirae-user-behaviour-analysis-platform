package com.admin.server.handler.analysis;

import com.admin.server.model.dto.EventAnalysisResultDto;
import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHandler implements AnalysisHandler{

    private final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private final AnalysisParam param;

    public EventHandler(AnalysisParam param) {
        this.param = param;
    }

    @Override
    public EventAnalysisResultDto execute(AnalysisParam param) {
        Pair<String, List<String>> sqlPair = getEventSql(param);

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            PreparedStatement statement = connection.prepareStatement(SQL);

            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }

            ResultSet resultSet = statement.executeQuery();

            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> item = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    item.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                list.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Pair<String, List<String>> getEventSql(AnalysisParam param) {
        Pair<String, List<String>> whereSqlPair = SqlUtil.getWhereSql(param.getWhereFilter());
        List<String> sqlArgs = whereSqlPair.getValue();

        Pair<String, List<String>> dateRangeSqlPair = SqlUtil.getDateRangeSql(param.getDateRange());
        sqlArgs.addAll(dateRangeSqlPair.getValue());

        String sql = whereSqlPair.getKey() + dateRangeSqlPair.getKey();

        for (int i = 0; i < param.getAggregations().size(); i++) {
            Pair<String, List<String>> sqlPair = SqlUtil.getAggregation(i, param, sqlArgs, sql);

        }

        // order by
        return Pair.of(sql, sqlArgs);
    }
}
