package com.fs.starter.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 小程序用户资料（昵称、头像）
 */
@Data
@Builder
public class WxUserProfileVO {

    private Long id;
    private String nickname;
    private String avatar;
}
