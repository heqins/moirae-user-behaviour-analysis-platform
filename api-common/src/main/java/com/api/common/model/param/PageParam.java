package com.api.common.model.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class PageParam {

    private Integer pageNum;

    private Integer pageSize;

}
