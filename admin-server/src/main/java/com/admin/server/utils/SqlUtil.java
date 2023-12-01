package com.admin.server.utils;

import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            return null;
        }

        @Override
        public Relation execute(AnalysisWhereFilterParam.Filter filter) {
            return null;
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

        return relation.toString();
    }

    public static String getDateRangeSql(Set<String> dateRange) {

        return "";
    }
}
