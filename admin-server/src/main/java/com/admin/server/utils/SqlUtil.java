package com.admin.server.utils;

import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SqlUtil {

    private final Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    static String DEFAULT_WHERE = "1=1";

    private static final String DATE_RANGE_SQL = " and event_date >= ? and event_date <= ?";

    private static final String EVENT_SQL = " and report_event in (?)";

    interface Relation {
        String toSql();

        Relation execute(AnalysisWhereFilterParam.Filter filter);
    }

    static class Or implements Relation {

        @Override
        public String toSql() {
            return null;
        }

        @Override
        public Relation execute(AnalysisWhereFilterParam.Filter filter) {
            return null;
        }
    }

    static class And implements Relation{

        StringBuilder sb;

        public And() {
            sb = new StringBuilder();
        }

        @Override
        public String toSql() {
            return "(" + sb.toString() + ")";
        }

        @Override
        public Relation execute(AnalysisWhereFilterParam.Filter filter) {
            if (filter == null || !filter.isValid()) {
                return this;
            }

            if (sb.length() != 0) {
                sb.append(" AND ");
            }

            sb.append(filter.getColumnName()).append(" ")
                    .append(filter.getComparator());

            return this;
        }
    }

    public static Pair<String, List<String>> getWhereSql(AnalysisWhereFilterParam whereFilter) {
        if (whereFilter == null) {
            return Pair.of("", new ArrayList<>());
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
                throw new IllegalArgumentException("");
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
            return Pair.of("", new ArrayList<>());
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

        Pair<String, String> groupArrPair = getGroupBySql(analysisParam);
        List<String> copyGroupArr = new ArrayList<>();

        if (StringUtils.isNotBlank(dateGroupSql)) {
            copyGroupArr.add(dateGroupSql);
        }

        if (StringUtils.isNotBlank(dateGroupCol)) {
            sqlArgs.add(dateGroupCol);
        }

        String groupBySql = "";
        if (!CollectionUtils.isEmpty(copyGroupArr)) {
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

                whereSql = whereSqlArgsPair.getKey();
                whereArgs = whereSqlArgsPair.getValue();

                String selectAttr = this.req.getZhibiaoArr().get(index).getSelectAttr();
                String col = String.format(" (%s) as %s ", CountUtil.get().get(selectAttr.get(1)).apply(selectAttr.get(0)), "amount");
                copyGroupArr.add(col);
                break;
            default:
                throw new IllegalArgumentException("未知指标类型");
        }

        sqlArgs.addAll(argsWith);

        sqlArgs.addAll(whereArgs);

        if (StringUtils.isNotBlank(whereSql)) {
            whereSql = " and " + whereSql;
        }

        String SQL = String.format(" from ( %s  select %s from event_log_detail%s where %s%s%s order by date_group ) ",
                withSql, String.join(",", groupCol), analysisParam.getAppId(), sql, whereSql, groupBySql, String.join(",", groupArr));

        if (!copyGroupArr.isEmpty()) {
            SQL = SQL + " group by " + String.join(",", copyGroupArr);
        }

        copyGroupArr.add("arrayMap((x, y) -> (x, y),groupArray(date_group),groupArray(amount)) as data_group");
        String eventNameDisplay = String.format("%s(%d)", zhibiao.getEventNameDisplay(), index + 1);
        this.eventNameDisplayArr.add(eventNameDisplay);
        copyGroupArr.add(String.format("'%s' as eventNameDisplay", eventNameDisplay), "count(1)  group_num", String.valueOf(index + 1) + " as serial_number");

        SQL = String.format("select %s%s limit 1000 ", String.join(",", copyGroupArr), SQL);

        args.addAll(this.args);

        return new Triple<>(SQL, args);
    }

    public static Pair<String, String> getCountTypeMap() {

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

    public static Pair<String, String> getGroupBySql(AnalysisParam analysisParam) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder colBuilder = new StringBuilder();

        for (String groupBy: analysisParam.getGroupBy()) {
            sqlBuilder.append(groupBy);
            colBuilder.append(String.format(" %s as %s", groupBy, groupBy));
        }

        return Pair.of(sqlBuilder.toString(), colBuilder.toString());
    }

    public static Pair<String, String> getDateGroupSql(String windowTimeFormat) {
        switch (windowTimeFormat) {
            case "按天":
                return Pair.of("date_group", " formatDateTime(event_date,'%Y年%m月%d日') as date_group ");
            case "按周":
                return Pair.of("date_group", " formatDateTime(event_date,'%Y年%m月%d日 %H点') as date_group ");
            case "按分钟":
                return Pair.of("date_group", " formatDateTime(event_date,'%Y年%m月%d日 %H点%M分') as date_group ");
            case "按小时":
                return Pair.of("date_group", " formatDateTime(event_date,'%Y年%m月 星期%u')  as date_group ");
            case "按月":
                return Pair.of("date_group", " formatDateTime(event_date,'%Y年%m月') as date_group");
            case "合计":
                return Pair.of("date_group", " '合计' as date_group ");
        }

        return Pair.of("", "");
    }

    public static void main(String[] args) {
        AnalysisWhereFilterParam filterParam = new AnalysisWhereFilterParam();
        filterParam.setRelation("AND");
        AnalysisWhereFilterParam.Filter filter = new AnalysisWhereFilterParam.Filter();
        filter.setColumnName("name");
        filter.setComparator(">=");
        filter.setValue("32");

        AnalysisWhereFilterParam.Filter filter2 = new AnalysisWhereFilterParam.Filter();
        filter2.setColumnName("age");
        filter2.setComparator("IN");
        filter2.setValue("[1, 2, 3]");

        AnalysisWhereFilterParam.Filter filter3 = new AnalysisWhereFilterParam.Filter();
        filter3.setColumnName("version");
        filter3.setComparator("<=");
        filter3.setValue("3.45.0");

        List<AnalysisWhereFilterParam.Filter> filterList = new ArrayList<>();
        filterParam.setFilters(filterList);

        filterParam.getFilters().add(filter);
        filterParam.getFilters().add(filter2);
        filterParam.getFilters().add(filter3);

        Pair<String, List<String>> res = getWhereSql(filterParam);
        System.out.println("test");
    }
}
