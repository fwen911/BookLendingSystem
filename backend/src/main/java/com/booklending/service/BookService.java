package com.booklending.service;

import com.booklending.model.Book;
import java.util.List;

public interface BookService {
    List<Book> findAll();
    Book findById(Integer id);
    Book findByCode(String code);
    Book save(Book book);
    void delete(Integer id);
    List<Book> findByCategory(Integer categoryId);
    List<Book> search(String keyword);
    List<Book> findAvailable();
    void updateStatus(Integer id, Integer status);
}