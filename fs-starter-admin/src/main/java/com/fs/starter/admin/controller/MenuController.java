package com.fs.starter.admin.controller;

import com.fs.starter.common.result.R;
import com.fs.starter.domain.entity.Menu;
import com.fs.starter.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单权限管理接口
 */
@RestController
@RequestMapping("/admin/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /** 菜单列表（平铺） */
    @GetMapping("/list")
    public R<List<Menu>> list() {
        return R.ok(menuService.listAllMenus());
    }

    /** 菜单树 */
    @GetMapping("/tree")
    public R<List<Map<String, Object>>> tree() {
        return R.ok(menuService.getMenuTree());
    }

    /** 新增菜单 */
    @PostMapping
    public R<Void> create(@RequestBody Menu menu) {
        menuService.createMenu(menu);
        return R.ok();
    }

    /** 编辑菜单 */
    @PutMapping
    public R<Void> update(@RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return R.ok();
    }

    /** 删除菜单 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
