package com.fs.starter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码 DTO
 */
@Data
public class ResetPasswordDTO {

    @NotBlank(message = "密码不能为空")
    private String password;
}
