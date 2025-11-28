package com.booklending.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "borrow_record")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "book_id")
    private Integer bookId;
    
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "borrow_time")
    private Date borrowTime;
    
    @Column(name = "return_time")
    private Date returnTime;
    
    @Column(name = "due_date")
    private Date dueDate;
    
    private Integer status; // 0借阅中，1已归还
    
    // 非数据库字段，用于关联查询
    @Transient
    private Book book;
    
    @Transient
    private User user;
}