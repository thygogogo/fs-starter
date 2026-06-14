package com.fs.starter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.domain.entity.Role;
import com.fs.starter.domain.entity.RoleMenu;
import com.fs.starter.mapper.RoleMapper;
import com.fs.starter.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色业务
 */
@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 查询全部角色
     */
    public List<Role> listRoles() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<Role>().orderByAsc(Role::getId)
        );
    }

    /**
     * 根据编码获取角色
     */
    public Role getRoleByCode(String code) {
        Role role = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>().eq(Role::getCode, code)
        );
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return role;
    }

    /**
     * 获取角色关联的菜单 ID 列表
     */
    public List<Long> getRoleMenuIds(String roleCode) {
        return roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleCode, roleCode)
        ).stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }

    /**
     * 更新角色菜单关联（先删后插）
     */
    @Transactional
    public void updateRoleMenus(String roleCode, List<Long> menuIds) {
        roleMenuMapper.delete(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleCode, roleCode)
        );
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleCode(roleCode);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
    }
}
