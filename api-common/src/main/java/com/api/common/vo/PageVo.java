package com.api.common.vo;

import lombok.Data;

@Data
public class PageVo<T> {

    private Integer currentNum;

    private Integer pageSize;

    private Long total;

    private Boolean hasNext;

    private T data;
}
