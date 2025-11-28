package com.booklending.dao;

import com.booklending.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    Optional<Book> findByCode(String code);
    List<Book> findByCategoryIdAndStatus(Integer categoryId, Integer status);
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);
    List<Book> findByCategoryId(Integer categoryId);
}