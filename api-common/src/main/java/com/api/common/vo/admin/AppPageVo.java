package com.api.common.vo.admin;

import com.api.common.vo.BasePageVo;
import lombok.Data;

import java.util.List;

@Data
public class AppPageVo extends BasePageVo {

    private List<AppVo> apps;

}
