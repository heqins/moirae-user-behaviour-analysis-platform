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
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private Long roleId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long lastLoginTime;

    private Integer status;

}
