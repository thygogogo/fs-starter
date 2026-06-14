package com.fs.starter.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.config.wechat.WechatConfig;
import com.fs.starter.domain.dto.UserProfileDTO;
import com.fs.starter.domain.dto.WxLoginDTO;
import com.fs.starter.domain.entity.WechatUser;
import com.fs.starter.domain.enums.StatusEnum;
import com.fs.starter.domain.vo.LoginVO;
import com.fs.starter.domain.vo.WxUserProfileVO;
import com.fs.starter.mapper.WechatUserMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信小程序用户业务
 */
@Slf4j
@Service
public class WechatUserService {

    @Autowired
    private WechatUserMapper wechatUserMapper;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private WxMaService wxMaService;

    /**
     * 微信小程序登录
     * 通过 code 换取 openId，再用 openId 匹配用户
     */
    public LoginVO login(WxLoginDTO dto) {
        String openId;
        if (wechatConfig.isMock()) {
            openId = "mock_openid_" + dto.getCode();
        } else {
            openId = code2Session(dto.getCode());
        }

        WechatUser user = wechatUserMapper.selectOne(
                new LambdaQueryWrapper<WechatUser>().eq(WechatUser::getOpenId, openId)
        );

        if (user == null) {
            // 新用户自动注册
            user = new WechatUser();
            user.setOpenId(openId);
            user.setNickname(resolveNickname(dto.getNickname()));
            user.setAvatar(dto.getAvatar());
            user.setStatus(StatusEnum.ENABLED);
            wechatUserMapper.insert(user);
        } else {
            boolean updated = false;
            if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
                user.setNickname(dto.getNickname());
                updated = true;
            }
            if (dto.getAvatar() != null && !dto.getAvatar().isBlank()) {
                user.setAvatar(dto.getAvatar());
                updated = true;
            }
            if (updated) {
                wechatUserMapper.updateById(user);
            }
        }

        if (user.getStatus() != StatusEnum.ENABLED) {
            throw new RuntimeException("账号已被禁用");
        }

        // Sa-Token 登录
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return LoginVO.builder()
                .token(tokenInfo.getTokenValue())
                .userId(user.getId())
                .username(user.getNickname())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    /**
     * 获取用户信息
     */
    public WechatUser getProfile(Long userId) {
        WechatUser user = wechatUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return user;
    }

    /**
     * 更新用户资料（昵称、头像等）
     */
    public WxUserProfileVO updateProfile(Long userId, UserProfileDTO dto) {
        WechatUser user = wechatUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (dto.getNickname() != null) {
            String nickname = dto.getNickname().trim();
            if (nickname.isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名不能为空");
            }
            if (nickname.length() > 20) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名不能超过20字");
            }
            user.setNickname(nickname);
        }
        if (dto.getAvatar() != null) {
            String avatar = dto.getAvatar().trim();
            if (avatar.isBlank()) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "头像地址不能为空");
            }
            if (!avatar.startsWith("http://") && !avatar.startsWith("https://")) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "头像地址无效");
            }
            user.setAvatar(avatar);
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone().trim());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail().trim());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress().trim());
        }
        if (dto.getGender() != null) {
            for (com.fs.starter.domain.enums.GenderEnum g : com.fs.starter.domain.enums.GenderEnum.values()) {
                if (g.getCode().equals(dto.getGender())) {
                    user.setGender(g);
                    break;
                }
            }
        }
        wechatUserMapper.updateById(user);
        return toProfileVO(user);
    }

    public WxUserProfileVO toProfileVO(WechatUser user) {
        return WxUserProfileVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    private String resolveNickname(String nickname) {
        return (nickname != null && !nickname.isBlank()) ? nickname : "微信用户";
    }

    /**
     * 调用微信 jscode2session 接口换取 openId
     */
    private String code2Session(String code) {
        try {
            WxMaJscode2SessionResult result = wxMaService.getUserService().getSessionInfo(code);
            return result.getOpenid();
        } catch (WxErrorException e) {
            log.error("微信 code2Session 调用失败: {}", e.getMessage(), e);
            throw new RuntimeException("微信登录失败，请稍后重试");
        }
    }
}
