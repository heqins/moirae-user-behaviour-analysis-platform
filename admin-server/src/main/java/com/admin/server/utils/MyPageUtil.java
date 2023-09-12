package com.admin.server.utils;

import com.api.common.model.vo.PageVo;

public class MyPageUtil {

    public static <T> PageVo<T> constructPageVo(Integer currentNum, Integer pageSize, Long total, T data) {
        PageVo<T> res = new PageVo<>();

        res.setPageSize(pageSize);
        res.setCurrentNum(currentNum);
        res.setTotal(total);
        res.setData(data);

        boolean hasNext = false;
        if (currentNum != null && pageSize != null && total != null) {
            hasNext = (long) currentNum * pageSize < total;
        }

        res.setHasNext(hasNext);

        return res;
    }
}
