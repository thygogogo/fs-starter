package com.fs.starter.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fs.starter.common.result.R;
import com.fs.starter.domain.dto.AdminCreateDTO;
import com.fs.starter.domain.dto.AdminUpdateDTO;
import com.fs.starter.domain.dto.ResetPasswordDTO;
import com.fs.starter.domain.entity.Admin;
import com.fs.starter.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员管理接口
 */
@RestController
@RequestMapping("/admin/system/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/list")
    public R<Page<Admin>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        return R.ok(adminService.listAdmins(page, size, username, phone, status));
    }

    @PostMapping
    public R<Void> create(@Valid @RequestBody AdminCreateDTO dto) {
        adminService.createAdmin(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@Valid @RequestBody AdminUpdateDTO dto) {
        adminService.updateAdmin(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return R.ok();
    }

    @PutMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDTO dto) {
        adminService.resetPassword(id, dto.getPassword());
        return R.ok();
    }

    @PutMapping("/{id}/toggle-status")
    public R<Void> toggleStatus(@PathVariable Long id) {
        adminService.toggleStatus(id);
        return R.ok();
    }
}
