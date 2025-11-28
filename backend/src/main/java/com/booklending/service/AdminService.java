package com.booklending.service;

import com.booklending.model.Admin;

import java.util.Optional;

/**
 * 管理员服务接口
 */
public interface AdminService {

    /**
     * 管理员登录
     */
    Admin login(String username, String password);

    /**
     * 根据用户名查询管理员
     */
    Optional<Admin> findByUsername(String username);

    /**
     * 保存管理员
     */
    Admin save(Admin admin);

    /**
     * 修改密码
     */
    void updatePassword(Long id, String newPassword);
}