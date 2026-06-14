package com.fs.starter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fs.starter.common.exception.BusinessException;
import com.fs.starter.common.result.ResultCode;
import com.fs.starter.domain.entity.Menu;
import com.fs.starter.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单权限业务
 */
@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 查询全部菜单（平铺，按 sort 排序）
     */
    public List<Menu> listAllMenus() {
        return menuMapper.selectList(
                new LambdaQueryWrapper<Menu>()
                        .orderByAsc(Menu::getParentId)
                        .orderByAsc(Menu::getSort)
        );
    }

    /**
     * 获取菜单树
     */
    public List<Map<String, Object>> getMenuTree() {
        List<Menu> allMenus = listAllMenus();
        return buildTree(allMenus, 0L);
    }

    private List<Map<String, Object>> buildTree(List<Menu> menus, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", menu.getId());
                node.put("parentId", menu.getParentId());
                node.put("name", menu.getName());
                node.put("permissionKey", menu.getPermissionKey());
                node.put("type", menu.getType());
                node.put("path", menu.getPath());
                node.put("icon", menu.getIcon());
                node.put("sort", menu.getSort());
                node.put("status", menu.getStatus());
                node.put("children", buildTree(menus, menu.getId()));
                tree.add(node);
            }
        }
        return tree;
    }

    /**
     * 新增菜单
     */
    public void createMenu(Menu menu) {
        menuMapper.insert(menu);
    }

    /**
     * 更新菜单
     */
    public void updateMenu(Menu menu) {
        menuMapper.updateById(menu);
    }

    /**
     * 删除菜单
     */
    public void deleteMenu(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 检查是否有子菜单
        Long childCount = menuMapper.selectCount(
                new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("请先删除子菜单");
        }
        menuMapper.deleteById(id);
    }

    /**
     * 查询所有权限标识
     */
    public List<String> getAllPermissionKeys() {
        return menuMapper.selectList(
                new LambdaQueryWrapper<Menu>()
                        .isNotNull(Menu::getPermissionKey)
                        .eq(Menu::getStatus, 1)
        ).stream().map(Menu::getPermissionKey).collect(Collectors.toList());
    }
}
