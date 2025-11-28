package com.booklending.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    
    private String phone;
    
    @Column(name = "class_name")
    private String className;
    
    private String password;
    
    private Integer status; // 1正常，0挂失
    
    @Column(name = "last_login")
    private Date lastLogin;
    
    @Column(name = "create_time")
    private Date createTime;
}