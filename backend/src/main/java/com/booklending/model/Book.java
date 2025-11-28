package com.booklending.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    
    private String author;
    
    private String code;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    private String publisher;
    
    @Column(name = "publish_date")
    private Date publishDate;
    
    private String isbn;
    
    private BigDecimal price;
    
    private String description;
    
    @Column(name = "cover_url")
    private String coverUrl;
    
    private Integer status; // 1在馆，0借出
    
    @Column(name = "create_time")
    private Date createTime;
    
    // 非数据库字段，用于关联查询
    @Transient
    private Category category;
}