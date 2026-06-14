package com.fs.starter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建管理员 DTO
 */
@Data
public class AdminCreateDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

    private String phone;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
}
