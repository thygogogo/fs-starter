package com.fs.starter.admin.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fs.starter.domain.entity.Admin;
import com.fs.starter.domain.entity.Menu;
import com.fs.starter.domain.entity.RoleMenu;
import com.fs.starter.domain.enums.RoleCodeEnum;
import com.fs.starter.mapper.AdminMapper;
import com.fs.starter.mapper.MenuMapper;
import com.fs.starter.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证实现
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Admin admin = adminMapper.selectById((Serializable) loginId);
        if (admin == null) {
            return Collections.emptyList();
        }

        // super_admin 拥有全部权限
        if (admin.getRoleCode() == RoleCodeEnum.SUPER_ADMIN) {
            return menuMapper.selectList(
                    new LambdaQueryWrapper<Menu>()
                            .isNotNull(Menu::getPermissionKey)
                            .eq(Menu::getStatus, 1)
            ).stream().map(Menu::getPermissionKey).collect(Collectors.toList());
        }

        // 查询角色关联的菜单 ID
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleCode, admin.getRoleCode().getCode())
        ).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询这些菜单的 permissionKey
        return menuMapper.selectBatchIds(menuIds).stream()
                .filter(m -> m.getPermissionKey() != null && !m.getPermissionKey().isBlank())
                .map(Menu::getPermissionKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Admin admin = adminMapper.selectById((Serializable) loginId);
        if (admin == null) {
            return Collections.emptyList();
        }
        return List.of(admin.getRoleCode().getCode());
    }
}
