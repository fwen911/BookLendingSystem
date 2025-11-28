package com.booklending.service.impl;

import com.booklending.dao.AdminRepository;
import com.booklending.model.Admin;
import com.booklending.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * 管理员登录
     */
    @Override
    public Admin login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 简单的密码校验（实际应该使用加密）
        if (!password.equals(admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return admin;
    }

    /**
     * 根据用户名查询管理员
     */
    @Override
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    /**
     * 保存管理员
     */
    @Override
    public Admin save(Admin admin) {
        if (admin.getId() == null) {
            admin.setCreateTime(LocalDateTime.now());
        }
        return adminRepository.save(admin);
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(Long id, String newPassword) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        admin.setPassword(newPassword);
        adminRepository.save(admin);
    }
}