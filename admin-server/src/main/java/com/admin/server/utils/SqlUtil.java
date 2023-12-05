package com.admin.server.utils;

import com.admin.server.model.domain.sql.And;
import com.admin.server.model.domain.sql.Or;
import com.admin.server.model.domain.sql.Relation;
import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SqlUtil {

    private final Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    private static final String DATE_RANGE_SQL = " AND event_date >= ? AND event_date <= ?";

    private static final String EVENT_SQL = " AND event_name in (?)";

    public static Pair<String, List<String>> getWhereSql(AnalysisWhereFilterParam whereFilter) {
        if (whereFilter == null || whereFilter.getFilters() == null) {
            return null;
        }

        Relation relation;
        switch (whereFilter.getRelation()) {
            case "AND":
                relation = new And();
                break;
            case "OR":
                relation = new Or();
                break;
            default:
                throw new IllegalArgumentException("关联关系参数错误");
        }

        List<String> sqlArgs = new ArrayList<>();

        for (AnalysisWhereFilterParam.Filter filter: whereFilter.getFilters()) {
            relation = relation.execute(filter);
            sqlArgs.add(filter.getValue());
        }

        String relationSql =  relation.toSql();
        return Pair.of(relationSql, sqlArgs);
    }

    public static Pair<String, List<String>> getDateRangeSql(List<String> dateRange) {
        if (dateRange.size() == 0 || dateRange.size() > 2) {
            return null;
        }

        String startTime = dateRange.get(0) + " 00:00:00";
        String endTime = dateRange.get(1) + " 23:59:59";

        List<String> sqlArgs = new ArrayList<>();

        sqlArgs.add(startTime);
        sqlArgs.add(endTime);

        return Pair.of(DATE_RANGE_SQL, sqlArgs);
    }

    public static Pair<String, List<String>> getAggregation(int index, AnalysisParam analysisParam, List<String> sqlArgs, String sql) {
        AnalysisAggregationParam agg = analysisParam.getAggregations().get(index);
        Pair<String, List<String>> eventSqlArgsPair = getEventAggSql(agg);

        sql += eventSqlArgsPair.getKey();
        sqlArgs.addAll(eventSqlArgsPair.getValue());

        Pair<String, String> dateGroupSqlColPair = getDateGroupSql(analysisParam.getWindowFormat());

        String dateGroupSql = dateGroupSqlColPair.getKey();
        String dateGroupCol = dateGroupSqlColPair.getValue();

        Pair<List<String>, List<String>> groupArrPair = getGroupBySql(analysisParam);

        List<String> groupArr = groupArrPair.getKey();
        List<String> groupCol = groupArrPair.getValue();

        List<String> copyGroupArr = new ArrayList<>();

        if (StringUtils.isNotBlank(dateGroupCol)) {
            groupCol.add(dateGroupCol);
        }

        if (StringUtils.isNotBlank(dateGroupSql)) {
            groupArr.add(dateGroupSql);
        }

        String groupBySql = "";
        if (!CollectionUtils.isEmpty(groupArr)) {
            groupBySql = " group by ";
        }

        String whereSql = "";
        List<String> whereArgs = new ArrayList<>();

        String withSql = "";
        List<String> argsWith = new ArrayList<>();

        switch (agg.getType()) {
            case "zhibiao":
                if (CollectionUtils.isEmpty(agg.getSelectAttributes())) {
                    throw new IllegalArgumentException("请选择维度");
                }

                Pair<String, List<String>> whereSqlArgsPair = getWhereSql(agg.getRelation());

                if (whereSqlArgsPair != null) {
                    whereSql = whereSqlArgsPair.getKey();
                    whereArgs = whereSqlArgsPair.getValue();
                }

                List<String> selectAttrs = agg.getSelectAttributes();
                String col = String.format(" (%s) as %s ", CountUtil.COUNT_TYPE_MAP.get(selectAttrs.get(1))
                        .apply(selectAttrs.get(0)), "amount");
                groupCol.add(col);
                break;
            default:
                throw new IllegalArgumentException("未知指标类型");
        }

        sqlArgs.addAll(argsWith);

        sqlArgs.addAll(whereArgs);

        if (StringUtils.isNotBlank(whereSql)) {
            whereSql = " and " + whereSql;
        }

        String SQL = String.format(" from ( %s select %s from event_log_detail_%s where %s%s%s",
                withSql, String.join(",", groupCol), analysisParam.getAppId(), sql,
                whereSql, groupBySql);

        SQL += String.join(",", groupArr);
        SQL += " order by date_group ) t";

        if (!CollectionUtils.isEmpty(copyGroupArr)) {
            SQL = SQL + " group by " + String.join(",", copyGroupArr);
        }

        copyGroupArr.add("ARRAY(t.date_group, t.amount) as data_group");
        String eventNameDisplay = String.format("%s(%d)", agg.getEventNameForDisplay(), index + 1);

//        this.eventNameDisplayArr.add(eventNameDisplay);

        copyGroupArr.add(String.format("'%s' as eventNameDisplay", eventNameDisplay));
        copyGroupArr.add("count(1) as group_num");
        copyGroupArr.add(index + 1 + " as serial_number");

        SQL = String.format("select %s%s group by date_group, amount limit 1000 ", String.join(",", copyGroupArr), SQL);

        //        sqlArgs.addAll(this.args);
        return Pair.of(SQL, sqlArgs);
    }

    public static Pair<String, List<String>> getEventAggSql(AnalysisAggregationParam aggregationParam) {
        List<String> sqlArg = new ArrayList<>();
        switch (aggregationParam.getType()) {
            case "":
                sqlArg.add(aggregationParam.getEventName());
                break;
            default:
                return Pair.of("", new ArrayList<>());
        }

        return Pair.of(EVENT_SQL, sqlArg);
    }

    public static Pair<List<String>, List<String>> getGroupBySql(AnalysisParam analysisParam) {
        List<String> groupArr = new ArrayList<>();
        List<String> groupCol = new ArrayList<>();

        if (!CollectionUtils.isEmpty(analysisParam.getGroupBy())) {
            for (String groupBy: analysisParam.getGroupBy()) {
                groupArr.add(groupBy);
                groupCol.add(String.format(" %s as %s", groupBy, groupBy));
            }
        }

        return Pair.of(groupArr, groupCol);
    }

    public static Pair<String, String> getDateGroupSql(String windowTimeFormat) {
        switch (windowTimeFormat) {
            case "按分钟":
                return Pair.of("date_group", " DATE_FORMAT(event_date,'%Y年%m月%d日 %H点%M分') as date_group ");
            case "按小时":
                return Pair.of("date_group", " DATE_FORMAT(event_date,'%Y年%m月%d日 %H点') as date_group ");
            case "按天":
                return Pair.of("date_group", " DATE_FORMAT(event_date,'%Y年%m月%d日') as date_group ");
            case "按周":
                return Pair.of("date_group", " DATE_FORMAT(event_date,'%Y年%m月 星期%u')  as date_group ");
            case "按月":
                return Pair.of("date_group", " DATE_FORMAT(event_date,'%Y年%m月') as date_group ");
            case "合计":
                return Pair.of("date_group", " '合计' as date_group ");
            default:
                break;
        }

        return Pair.of("", "");
    }
}
