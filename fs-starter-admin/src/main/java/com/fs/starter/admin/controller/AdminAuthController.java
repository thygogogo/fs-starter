package com.fs.starter.admin.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.crypto.digest.BCrypt;
import com.fs.starter.common.result.R;
import com.fs.starter.domain.dto.AdminLoginDTO;
import com.fs.starter.domain.vo.LoginVO;
import com.fs.starter.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证接口
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    /**
     * 管理员登录
     *
     * @param dto 登录参数（用户名 + 密码）
     * @return 登录信息（token、用户信息）
     */
    @SaIgnore
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody AdminLoginDTO dto) {
        return R.ok(adminService.login(dto));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/info")
    public R<LoginVO> info() {
        return R.ok(adminService.getCurrentUserInfo());
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        adminService.logout();
        return R.ok();
    }

    /**
     * 生成 BCrypt 密码哈希（临时工具接口，用完可删除）
     */
    @SaIgnore
    @GetMapping("/hash")
    public R<String> hashPassword(@RequestParam String password) {
        return R.ok(BCrypt.hashpw(password, BCrypt.gensalt()));
    }
}
