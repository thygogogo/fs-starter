package com.fs.starter.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.domain.dto.AdminCreateDTO;
import com.fs.starter.domain.dto.AdminUpdateDTO;
import com.fs.starter.domain.dto.AdminLoginDTO;
import com.fs.starter.domain.entity.Admin;
import com.fs.starter.domain.entity.Menu;
import com.fs.starter.domain.entity.RoleMenu;
import com.fs.starter.domain.enums.RoleCodeEnum;
import com.fs.starter.domain.enums.StatusEnum;
import com.fs.starter.domain.vo.LoginVO;
import com.fs.starter.mapper.AdminMapper;
import com.fs.starter.mapper.MenuMapper;
import com.fs.starter.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员业务
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 管理员登录
     */
    public LoginVO login(AdminLoginDTO dto) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername())
        );
        if (admin == null) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (admin.getStatus() != StatusEnum.ENABLED) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // Sa-Token 登录，使用 admin 的 id 作为登录标识
        StpUtil.login(admin.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return LoginVO.builder()
                .token(tokenInfo.getTokenValue())
                .userId(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .avatar(admin.getAvatar())
                .roleCode(admin.getRoleCode().getCode())
                .permissions(getPermissionsByRoleCode(admin.getRoleCode()))
                .build();
    }

    /**
     * 获取当前登录用户信息
     */
    public LoginVO getCurrentUserInfo() {
        Long adminId = StpUtil.getLoginIdAsLong();
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return LoginVO.builder()
                .userId(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .avatar(admin.getAvatar())
                .roleCode(admin.getRoleCode().getCode())
                .permissions(getPermissionsByRoleCode(admin.getRoleCode()))
                .build();
    }

    /**
     * 退出登录
     */
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 校验当前登录用户为超级管理员
     */
    public void requireSuperAdmin() {
        Long adminId = StpUtil.getLoginIdAsLong();
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getRoleCode() != RoleCodeEnum.SUPER_ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 加密密码（供初始化数据使用）
     */
    public static String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 分页查询管理员列表
     */
    public Page<Admin> listAdmins(int page, int size, String username, String phone, Integer status) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isBlank()) {
            wrapper.like(Admin::getUsername, username);
        }
        if (phone != null && !phone.isBlank()) {
            wrapper.like(Admin::getPhone, phone);
        }
        if (status != null) {
            wrapper.eq(Admin::getStatus, status);
        }
        wrapper.orderByDesc(Admin::getCreateTime);
        return adminMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 创建管理员
     */
    public void createAdmin(AdminCreateDTO dto) {
        // 检查用户名是否已存在
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        admin.setNickname(dto.getNickname());
        admin.setPhone(dto.getPhone());
        admin.setRoleCode(RoleCodeEnum.valueOf(dto.getRoleCode().toUpperCase()));
        admin.setStatus(StatusEnum.ENABLED);
        adminMapper.insert(admin);
    }

    /**
     * 更新管理员
     */
    public void updateAdmin(AdminUpdateDTO dto) {
        Admin admin = adminMapper.selectById(dto.getId());
        if (admin == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (dto.getNickname() != null) {
            admin.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            admin.setPhone(dto.getPhone());
        }
        if (dto.getRoleCode() != null) {
            admin.setRoleCode(RoleCodeEnum.valueOf(dto.getRoleCode().toUpperCase()));
        }
        adminMapper.updateById(admin);
    }

    /**
     * 删除管理员
     */
    public void deleteAdmin(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 不能删除自己
        Long currentId = StpUtil.getLoginIdAsLong();
        if (id.equals(currentId)) {
            throw new BusinessException("不能删除自己的账号");
        }
        adminMapper.deleteById(id);
    }

    /**
     * 重置密码
     */
    public void resetPassword(Long id, String newPassword) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        Admin update = new Admin();
        update.setId(id);
        update.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        adminMapper.updateById(update);
    }

    /**
     * 切换启用/禁用状态
     */
    public void toggleStatus(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 不能禁用自己
        Long currentId = StpUtil.getLoginIdAsLong();
        if (id.equals(currentId)) {
            throw new BusinessException("不能禁用自己的账号");
        }
        admin.setStatus(admin.getStatus() == StatusEnum.ENABLED ? StatusEnum.DISABLED : StatusEnum.ENABLED);
        adminMapper.updateById(admin);
    }

    /**
     * 根据角色编码获取权限列表
     */
    private List<String> getPermissionsByRoleCode(RoleCodeEnum roleCode) {
        if (roleCode == RoleCodeEnum.SUPER_ADMIN) {
            return menuMapper.selectList(
                    new LambdaQueryWrapper<Menu>()
                            .isNotNull(Menu::getPermissionKey)
                            .eq(Menu::getStatus, 1)
            ).stream().map(Menu::getPermissionKey).collect(Collectors.toList());
        }

        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleCode, roleCode.getCode())
        ).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        return menuMapper.selectBatchIds(menuIds).stream()
                .filter(m -> m.getPermissionKey() != null && !m.getPermissionKey().isBlank())
                .map(Menu::getPermissionKey)
                .collect(Collectors.toList());
    }
}
