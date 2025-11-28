package com.booklending.service;

import com.booklending.model.BorrowRecord;

import java.util.List;
import java.util.Optional;

/**
 * 借阅记录服务接口
 */
public interface BorrowService {

    /**
     * 创建借阅记录
     */
    BorrowRecord borrowBook(Long bookId, String phone);

    /**
     * 归还图书
     */
    BorrowRecord returnBook(Long bookId, String phone);

    /**
     * 续借图书
     */
    BorrowRecord renewBook(Long recordId);

    /**
     * 获取所有借阅记录
     */
    List<BorrowRecord> findAll();

    /**
     * 根据ID查询借阅记录
     */
    Optional<BorrowRecord> findById(Long id);

    /**
     * 查询用户的借阅记录
     */
    List<BorrowRecord> findByUserId(Long userId);

    /**
     * 查询用户的未还记录
     */
    List<BorrowRecord> findActiveByUserId(Long userId);

    /**
     * 查询图书的借阅记录
     */
    List<BorrowRecord> findByBookId(Long bookId);

    /**
     * 查找用户对特定图书的未还记录
     */
    Optional<BorrowRecord> findActiveByUserIdAndBookId(Long userId, Long bookId);

    /**
     * 查找用户最新的借阅记录
     */
    Optional<BorrowRecord> findLatestByUserIdAndBookId(Long userId, Long bookId);
}
