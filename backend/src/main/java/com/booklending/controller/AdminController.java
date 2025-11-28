package com.booklending.controller;

import com.booklending.model.Admin;
import com.booklending.service.AdminService;
import com.booklending.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Response<Admin> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            if (username == null || password == null) {
                return Response.fail("用户名和密码不能为空");
            }
            
            Admin admin = adminService.login(username, password);
            return Response.success(admin);
        } catch (RuntimeException e) {
            return Response.fail(e.getMessage());
        } catch (Exception e) {
            return Response.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 修改管理员密码
     */
    @PatchMapping("/{id}/password")
    public Response<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        try {
            String newPassword = passwordData.get("newPassword");
            if (newPassword == null || newPassword.isEmpty()) {
                return Response.fail("新密码不能为空");
            }
            
            adminService.updatePassword(id, newPassword);
            return Response.success("密码修改成功");
        } catch (RuntimeException e) {
            return Response.fail(e.getMessage());
        } catch (Exception e) {
            return Response.fail("密码修改失败: " + e.getMessage());
        }
    }
}