package com.fs.starter.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 登录响应
 */
@Data
@Builder
public class LoginVO {

    /** Sa-Token token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 角色编码 */
    private String roleCode;

    /** 权限列表 */
    private List<String> permissions;
}
