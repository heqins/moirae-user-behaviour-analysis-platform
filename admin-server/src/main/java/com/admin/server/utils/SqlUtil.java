package com.admin.server.utils;

import com.api.common.model.param.admin.AnalysisAggregationParam;
import com.api.common.model.param.admin.AnalysisParam;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        Pair<String, String> dateGroupSqlColPair = getGroupDateSql();

        String dateGroupSql = dateGroupSqlColPair.getFirst();
        String dateGroupCol = dateGroupSqlColPair.getSecond();

        List<String> groupArr = this.getGroupSql();
        List<String> copyGroupArr = new ArrayList<>(groupArr);

        if (!dateGroupCol.isEmpty()) {
            groupCol.add(dateGroupCol);
        }

        if (!dateGroupSql.isEmpty()) {
            groupArr.add(dateGroupSql);
        }

        String groupBySql = "";
        if (!groupArr.isEmpty()) {
            groupBySql = " group by ";
        }

        String whereSql = "";
        List<Object> whereArgs = new ArrayList<>();

        String withSql = "";
        List<Object> argsWith = new ArrayList<>();

        switch (zhibiao.getTyp()) {
            case Zhibiao:
                if (zhibiao.getSelectAttr().isEmpty()) {
                    throw new Exception("请选择维度");
                }

                Pair<String, List<Object>> whereSqlArgsPair = utils.getWhereSql(zhibiao.getRelation());
                whereSql = whereSqlArgsPair.getFirst();
                whereArgs = whereSqlArgsPair.getSecond();

                String selectAttr = this.req.getZhibiaoArr().get(index).getSelectAttr();
                String col = String.format(" (%s) as %s ", utils.getCountTypMap().get(selectAttr.get(1)).apply(selectAttr.get(0)), "amount");
                groupCol.add(col);
                break;
            case Formula:
                if (zhibiao.getOne().getSelectAttr().isEmpty() || zhibiao.getTwo().getSelectAttr().isEmpty()) {
                    throw new Exception("请选择维度");
                }

                Pair<String, String> formulaSqlOnePair = this.getFormulaSql(zhibiao.getOne(), false, zhibiao.getDivisorNoGrouping());
                String sqlOne = formulaSqlOnePair.getSecond();
                List<Object> argsOne = formulaSqlOnePair.getThird();

                Pair<String, String> formulaSqlTwoPair = this.getFormulaSql(zhibiao.getTwo(), true, zhibiao.getDivisorNoGrouping());
                withSql = formulaSqlTwoPair.getFirst();
                String sqlTwo = formulaSqlTwoPair.getSecond();
                List<Object> argsTwo = formulaSqlTwoPair.getThird();

                List<Object> argsTmp = new ArrayList<>();
                argsTmp.addAll(argsOne);
                argsTmp.addAll(argsTwo);

                args.addAll(argsTmp);

                switch (zhibiao.getScaleType()) {
                    case utils.TwoDecimalPlaces:
                        String fmtStr = String.format("  %s(%s,%s) ", zhibiao.getOperate(), utils.toFloat32OrZero(sqlOne), utils.toFloat32OrZero(sqlTwo));
                        groupCol.add(String.format("toString(%s)   amount", utils.round(utils.NaN2Zero(fmtStr))));
                        break;
                    case utils.Percentage:
                        String fmtStr = String.format("  %s(%s,%s) ", zhibiao.getOperate(), utils.toFloat32OrZero(sqlOne), utils.toFloat32OrZero(sqlTwo));
                        groupCol.add(String.format(" concat(toString( %s *100),'%%')   amount", utils.round(utils.NaN2Zero(fmtStr))));
                        break;
                    case utils.Rounding:
                        String fmtStr = String.format("  %s(%s,%s) ", zhibiao.getOperate(), utils.toFloat32OrZero(sqlOne), utils.toFloat32OrZero(sqlTwo));
                        groupCol.add(String.format("toString(round(%s,0))   amount", utils.NaN2Zero(fmtStr)));
                        break;
                }
                break;
            default:
                throw new Exception("未知指标类型");
        }

        argsWith.addAll(args);

        args.addAll(whereArgs);

        if (!whereSql.isEmpty()) {
            whereSql = " and " + whereSql;
        }

        String SQL = String.format(" from ( %s  select %s from xwl_event%s prewhere %s%s%s%s order by date_group ) ",
                withSql, String.join(",", groupCol), this.req.getAppid(), sql, whereSql, this.sql, groupBySql, String.join(",", groupArr));

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

    public static Pair<String, String> getDateGroupSql(String windowTimeFormat) {
        switch (windowTimeFormat) {
            case "按天":
                return Pair.of("date_group", "");
            case "按周":
                return Pair.of("date_group", "");
            case "按分钟":
                return Pair.of("date_group", "");
            case "按小时":
                return Pair.of("date_group", "");
            case "按月":
                return Pair.of("date_group", "");
            case "合计":
                return Pair.of("date_group", "");
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
