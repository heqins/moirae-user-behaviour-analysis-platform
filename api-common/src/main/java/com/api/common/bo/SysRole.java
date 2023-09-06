package com.api.common.bo;

import com.api.common.param.admin.CreateAppParam;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author heqin
 */
@Data
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
