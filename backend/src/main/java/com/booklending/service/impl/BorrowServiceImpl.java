package com.booklending.service.impl;

import com.booklending.dao.BookRepository;
import com.booklending.dao.BorrowRecordRepository;
import com.booklending.dao.UserRepository;
import com.booklending.model.Book;
import com.booklending.model.BorrowRecord;
import com.booklending.model.User;
import com.booklending.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * 借阅记录服务实现类
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 借阅图书
     */
    @Override
    @Transactional
    public BorrowRecord borrowBook(Long bookId, String phone) {
        // 检查图书是否存在且可借
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        
        if (book.getStatus() == 0) {
            throw new RuntimeException("图书已被借出");
        }

        // 查找或创建用户
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhone(phone);
                    newUser.setName("用户" + phone.substring(7));
                    newUser.setStatus(1);
                    newUser.setCreateTime(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被挂失，无法借阅");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(user.getId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));
        record.setStatus(0); // 0表示未还

        // 更新图书状态
        book.setStatus(0);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    /**
     * 归还图书
     */
    @Override
    @Transactional
    public BorrowRecord returnBook(Long bookId, String phone) {
        // 查找用户
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 查找未还记录
        BorrowRecord record = borrowRecordRepository.findByUserIdAndBookIdAndStatus(user.getId(), bookId, 0)
                .orElseThrow(() -> new RuntimeException("未找到该用户对此图书的借阅记录"));

        // 更新借阅记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(1); // 1表示已还

        // 更新图书状态
        Book book = bookRepository.findById(bookId).orElseThrow();
        book.setStatus(1);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    /**
     * 续借图书
     */
    @Override
    @Transactional
    public BorrowRecord renewBook(Long recordId) {
        // 查找借阅记录
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        // 检查是否可续借
        if (record.getStatus() != 0) {
            throw new RuntimeException("该图书已归还，无法续借");
        }

        // 检查是否已过期
        if (record.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("图书已过期，无法续借");
        }

        // 续借30天
        record.setDueDate(record.getDueDate().plusDays(30));

        return borrowRecordRepository.save(record);
    }

    /**
     * 获取所有借阅记录
     */
    @Override
    public List<BorrowRecord> findAll() {
        return borrowRecordRepository.findAll();
    }

    /**
     * 根据ID查询借阅记录
     */
    @Override
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    /**
     * 查询用户的借阅记录
     */
    @Override
    public List<BorrowRecord> findByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }

    /**
     * 查询用户的未还记录
     */
    @Override
    public List<BorrowRecord> findActiveByUserId(Long userId) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, 0);
    }

    /**
     * 查询图书的借阅记录
     */
    @Override
    public List<BorrowRecord> findByBookId(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId);
    }

    /**
     * 查找用户对特定图书的未还记录
     */
    @Override
    public Optional<BorrowRecord> findActiveByUserIdAndBookId(Long userId, Long bookId) {
        return borrowRecordRepository.findByUserIdAndBookIdAndStatus(userId, bookId, 0);
    }

    /**
     * 查找用户最新的借阅记录
     */
    @Override
    public Optional<BorrowRecord> findLatestByUserIdAndBookId(Long userId, Long bookId) {
        return borrowRecordRepository.findTopByUserIdAndBookIdOrderByBorrowTimeDesc(userId, bookId);
    }
}