package com.fs.starter.app.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fs.starter.common.result.R;
import com.fs.starter.domain.dto.UserProfileDTO;
import com.fs.starter.domain.entity.WechatUser;
import com.fs.starter.domain.vo.WxUserProfileVO;
import com.fs.starter.service.WechatUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序用户信息接口
 */
@RestController
@RequestMapping("/app/user")
@RequiredArgsConstructor
@Validated
public class WechatUserController {

    private final WechatUserService wechatUserService;

    @GetMapping("/profile")
    public R<WechatUser> profile() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(wechatUserService.getProfile(userId));
    }

    @PutMapping("/profile")
    public R<WxUserProfileVO> updateProfile(@Valid @RequestBody UserProfileDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(wechatUserService.updateProfile(userId, dto));
    }
}
