package com.api.common.vo;

import lombok.Data;

@Data
public class BasePageVo {

    private Integer currentNum;

    private Integer pageSize;

    private Long total;

    private Boolean hasNext;

}
