package com.fs.starter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序登录请求
 */
@Data
public class WxLoginDTO {

    @NotBlank(message = "code 不能为空")
    private String code;

    /** 微信昵称 */
    private String nickname;

    /** 微信头像 URL */
    private String avatar;
}
