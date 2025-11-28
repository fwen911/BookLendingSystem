package com.booklending.service;

import com.booklending.model.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User findByPhone(String phone);
    User save(User user);
    void delete(Integer id);
    List<User> search(String keyword);
    void updateStatus(Integer id, Integer status);
    void updatePassword(Integer id, String password);
}
