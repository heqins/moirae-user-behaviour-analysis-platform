package com.admin.server.util;

import com.api.common.vo.PageVo;

public class MyPageUtil {

    public static <T> PageVo<T> constructPageVo(Integer currentNum, Integer pageSize, Long total, T data) {
        PageVo<T> res = new PageVo<>();

        res.setPageSize(pageSize);
        res.setCurrentNum(currentNum);
        res.setTotal(total);
        res.setData(data);

        boolean hasNext = (long) currentNum * pageSize > total;

        return res;
    }
}
