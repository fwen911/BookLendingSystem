package com.booklending.dao;

import com.booklending.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {
    List<BorrowRecord> findByUserIdAndStatus(Integer userId, Integer status);
    List<BorrowRecord> findByBookIdAndStatus(Integer bookId, Integer status);
    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, Integer status);
    List<BorrowRecord> findByBookId(Integer bookId);
    List<BorrowRecord> findByUserId(Integer userId);
}