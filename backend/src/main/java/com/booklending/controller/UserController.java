package com.booklending.controller;

import com.booklending.model.User;
import com.booklending.service.UserService;
import com.booklending.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public Response<List<User>> getUserList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status) {
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userService.search(search);
        } else {
            users = userService.findAll();
        }
        // 如果指定了状态，进行过滤
        if (status != null) {
            users = users.stream().filter(user -> user.getStatus().equals(status)).toList();
        }
        return Response.success(users);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Response<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(Response::success).orElse(Response.fail("用户不存在"));
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Response<User> addUser(@RequestBody User user) {
        try {
            User savedUser = userService.save(user);
            return Response.success(savedUser);
        } catch (Exception e) {
            return Response.fail("新增用户失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/{id}")
    public Response<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // 确保ID一致
            user.setId(id);
            User updatedUser = userService.save(user);
            return Response.success(updatedUser);
        } catch (Exception e) {
            return Response.fail("修改用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Response<String> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return Response.success("删除成功");
        } catch (Exception e) {
            return Response.fail("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态（挂失/解挂）
     */
    @PatchMapping("/{id}/status")
    public Response<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            userService.updateStatus(id, status);
            return Response.success("状态更新成功");
        } catch (Exception e) {
            return Response.fail("状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     */
    @PatchMapping("/{id}/password")
    public Response<String> updateUserPassword(@PathVariable Long id, @RequestBody String newPassword) {
        try {
            userService.updatePassword(id, newPassword);
            return Response.success("密码修改成功");
        } catch (Exception e) {
            return Response.fail("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 根据手机号查询用户
     */
    @GetMapping("/phone/{phone}")
    public Response<User> getUserByPhone(@PathVariable String phone) {
        Optional<User> user = userService.findByPhone(phone);
        return user.map(Response::success).orElse(Response.fail("用户不存在"));
    }
}