package com.fs.starter.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新管理员 DTO
 */
@Data
public class AdminUpdateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    private String nickname;

    private String phone;

    private String roleCode;
}
