package com.fs.starter.domain.dto;

import lombok.Data;

/**
 * 用户信息更新DTO
 */
@Data
public class UserProfileDTO {

    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private String address;
    private Integer gender;
}
