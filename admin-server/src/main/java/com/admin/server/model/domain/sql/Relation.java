package com.admin.server.model.domain.sql;

import com.admin.server.utils.SqlUtil;
import com.api.common.model.param.admin.AnalysisWhereFilterParam;

public interface Relation {
    String toSql();

    Relation execute(AnalysisWhereFilterParam.Filter filter);
}
