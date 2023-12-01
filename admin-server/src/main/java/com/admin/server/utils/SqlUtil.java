package com.admin.server.utils;

import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SqlUtil {

    static String DEFAULT_WHERE = "1=1";

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
            return sb.toString();
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
                    .append(filter.getComparator()).append(" ")
                    .append(filter.getValue());

            return this;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(SqlUtil.class);

    public static String getWhereSql(AnalysisWhereFilterParam whereFilter) {
        if (whereFilter == null) {
            return DEFAULT_WHERE;
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

        for (AnalysisWhereFilterParam.Filter filter: whereFilter.getFilters()) {
            relation = relation.execute(filter);
        }

        return relation.toSql();
    }

    public static String getDateRangeSql(Set<String> dateRange) {

        return "";
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

        String whereSql = getWhereSql(filterParam);
        System.out.println(whereSql);
    }
}
