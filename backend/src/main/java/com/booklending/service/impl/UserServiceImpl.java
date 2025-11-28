package com.booklending.service.impl;

import com.booklending.dao.UserRepository;
import com.booklending.model.User;
import com.booklending.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }
    
    @Override
    public User save(User user) {
        // 如果是新用户，设置创建时间
        if (user.getId() == null) {
            user.setCreateTime(new Date());
            // 默认密码
            if (user.getPassword() == null) {
                user.setPassword("123456");
            }
        }
        return userRepository.save(user);
    }
    
    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public List<User> search(String keyword) {
        // 尝试将关键词转换为ID
        try {
            Integer id = Integer.parseInt(keyword);
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                return List.of(user);
            }
        } catch (NumberFormatException e) {
            // 不是数字，继续按名称搜索
        }
        
        // 这里简化处理，实际应该在Repository中添加更复杂的查询方法
        return userRepository.findAll().stream()
                .filter(user -> user.getName().contains(keyword))
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateStatus(Integer id, Integer status) {
        User user = findById(id);
        if (user != null) {
            user.setStatus(status);
            userRepository.save(user);
        }
    }
    
    @Override
    public void updatePassword(Integer id, String password) {
        User user = findById(id);
        if (user != null) {
            user.setPassword(password);
            userRepository.save(user);
        }
    }
}