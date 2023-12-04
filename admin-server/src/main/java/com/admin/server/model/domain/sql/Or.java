package com.admin.server.model.domain.sql;

import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;

public class Or implements Relation{
    @Override
    public String toSql() {
        return null;
    }

    @Override
    public Relation execute(AnalysisWhereFilterParam.Filter filter) {
        return null;
    }
}
