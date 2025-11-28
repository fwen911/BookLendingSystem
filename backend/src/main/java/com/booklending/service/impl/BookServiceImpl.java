package com.booklending.service.impl;

import com.booklending.dao.BookRepository;
import com.booklending.model.Book;
import com.booklending.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * 获取所有图书
     */
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * 根据ID查询图书
     */
    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * 根据编码查询图书
     */
    @Override
    public Optional<Book> findByCode(String code) {
        return bookRepository.findByCode(code);
    }

    /**
     * 保存图书
     */
    @Override
    public Book save(Book book) {
        // 确保新书默认为在馆状态
        if (book.getId() == null) {
            book.setStatus(1);
        }
        return bookRepository.save(book);
    }

    /**
     * 删除图书
     */
    @Override
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * 根据分类查询图书
     */
    @Override
    public List<Book> findByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    /**
     * 搜索图书（按标题或作者模糊查询）
     */
    @Override
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword);
    }

    /**
     * 查询可用图书
     */
    @Override
    public List<Book> findAvailable() {
        return bookRepository.findByStatus(1);
    }

    /**
     * 更新图书状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        book.setStatus(status);
        bookRepository.save(book);
    }

    /**
     * 根据分类和状态查询图书
     */
    @Override
    public List<Book> findByCategoryAndStatus(Long categoryId, Integer status) {
        return bookRepository.findByCategoryIdAndStatus(categoryId, status);
    }
}