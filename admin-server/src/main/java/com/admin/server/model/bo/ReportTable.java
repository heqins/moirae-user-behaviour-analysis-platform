package com.admin.server.model.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
public class ReportTable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appId;

    private String uid;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String name;

    private String data;

    private String remark;

}
