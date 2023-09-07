package com.admin.server.util;

import com.api.common.vo.PageVo;

public class MyPageUtil {

    public static <T> PageVo<T> constructPageVo(Integer currentNum, Integer pageSize, Long total, Boolean hasNext, T data) {
        PageVo<T> res = new PageVo<>();

        res.setPageSize(pageSize);
        res.setCurrentNum(currentNum);
        res.setTotal(total);
        res.setData(data);

        return res;
    }
}
