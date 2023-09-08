package com.api.common.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageVo<T> {

    private Integer currentNum;

    private Integer pageSize;

    private Long total;

    private Boolean hasNext;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
}
