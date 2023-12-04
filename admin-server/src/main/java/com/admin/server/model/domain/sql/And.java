package com.admin.server.model.domain.sql;

import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;
import com.baomidou.mybatisplus.extension.api.R;

public class And implements Relation {
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
                .append(filter.getComparator()).append(" ?");

        return this;
    }
}
