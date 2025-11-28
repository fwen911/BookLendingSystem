package com.booklending.controller;

import com.booklending.model.Book;
import com.booklending.service.BookService;
import com.booklending.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取图书列表
     */
    @GetMapping
    public Response<List<Book>> getBookList(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String search) {
        List<Book> books;
        if (category != null) {
            books = bookService.findByCategory(category);
        } else if (search != null && search.length() >= 2) {
            books = bookService.search(search);
        } else {
            books = bookService.findAll();
        }
        return Response.success(books);
    }

    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Response<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        return book.map(Response::success).orElse(Response.fail("图书不存在"));
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Response<Book> addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.save(book);
            return Response.success(savedBook);
        } catch (Exception e) {
            return Response.fail("新增图书失败: " + e.getMessage());
        }
    }

    /**
     * 修改图书
     */
    @PutMapping("/{id}")
    public Response<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            // 确保ID一致
            book.setId(id);
            Book updatedBook = bookService.save(book);
            return Response.success(updatedBook);
        } catch (Exception e) {
            return Response.fail("修改图书失败: " + e.getMessage());
        }
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Response<String> deleteBook(@PathVariable Long id) {
        try {
            bookService.delete(id);
            return Response.success("删除成功");
        } catch (Exception e) {
            return Response.fail("删除图书失败: " + e.getMessage());
        }
    }

    /**
     * 获取可用图书
     */
    @GetMapping("/available")
    public Response<List<Book>> getAvailableBooks() {
        List<Book> books = bookService.findAvailable();
        return Response.success(books);
    }

    /**
     * 根据编码查询图书
     */
    @GetMapping("/code/{code}")
    public Response<Book> getBookByCode(@PathVariable String code) {
        Optional<Book> book = bookService.findByCode(code);
        return book.map(Response::success).orElse(Response.fail("图书不存在"));
    }
}