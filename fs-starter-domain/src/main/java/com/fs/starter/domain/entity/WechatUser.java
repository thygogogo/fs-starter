package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fs.starter.domain.enums.GenderEnum;
import com.fs.starter.domain.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信小程序用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wx_user")
public class WechatUser extends BaseDomain {

    /** 微信 openId */
    private String openId;

    /** 微信 unionId */
    private String unionId;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 地址 */
    private String address;

    /** 性别 */
    private GenderEnum gender;

    /** 状态 */
    private StatusEnum status;
}
