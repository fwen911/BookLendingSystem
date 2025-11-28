-- 创建图书借阅管理系统数据库
CREATE DATABASE IF NOT EXISTS book_lending DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE book_lending;

-- 创建图书分类表
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '图书分类表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    class_name VARCHAR(100) COMMENT '班级',
    password VARCHAR(255) DEFAULT '123456' COMMENT '密码（默认123456）',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0挂失',
    last_login DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '用户表';

-- 创建图书表
CREATE TABLE IF NOT EXISTS book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '图书号',
    category_id INT COMMENT '分类ID',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版时间',
    isbn VARCHAR(20) COMMENT 'ISBN',
    price DECIMAL(10,2) COMMENT '价格',
    description TEXT COMMENT '简介',
    cover_url VARCHAR(255) DEFAULT 'default_cover.png' COMMENT '封面图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1在馆，0借出',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (category_id) REFERENCES category(id)
) COMMENT '图书表';

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL COMMENT '图书ID',
    user_id INT NOT NULL COMMENT '用户ID',
    borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_time DATETIME COMMENT '归还时间',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0借阅中，1已归还',
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT '借阅记录表';

-- 创建管理员表
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '管理员表';

-- 插入默认管理员数据
INSERT INTO admin (username, password) VALUES ('admin', 'admin123') ON DUPLICATE KEY UPDATE username=username;

-- 插入默认分类数据
INSERT INTO category (name) VALUES 
('文学'),
('科技'),
('教育'),
('历史'),
('艺术'),
('生活'),
('经管'),
('哲学')
ON DUPLICATE KEY UPDATE name=name;