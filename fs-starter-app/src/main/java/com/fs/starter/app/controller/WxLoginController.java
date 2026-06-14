package com.fs.starter.app.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.fs.starter.common.result.R;
import com.fs.starter.domain.dto.WxLoginDTO;
import com.fs.starter.domain.vo.LoginVO;
import com.fs.starter.service.WechatUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序登录接口
 */
@RestController
@RequestMapping("/app/wx")
@RequiredArgsConstructor
public class WxLoginController {

    private final WechatUserService wechatUserService;

    /**
     * 微信小程序登录
     *
     * @param dto 登录参数（微信 code）
     * @return 登录信息（token、用户信息）
     */
    @SaIgnore
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody WxLoginDTO dto) {
        return R.ok(wechatUserService.login(dto));
    }
}
