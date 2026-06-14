package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fs.starter.domain.enums.RoleCodeEnum;
import com.fs.starter.domain.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台管理员
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_admin")
public class Admin extends BaseDomain {

    /** 用户名 */
    private String username;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 头像 URL */
    private String avatar;

    /** 角色编码 */
    private RoleCodeEnum roleCode;

    /** 状态 */
    private StatusEnum status;
}
