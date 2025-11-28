package com.booklending.controller;

import com.booklending.model.BorrowRecord;
import com.booklending.service.BorrowService;
import com.booklending.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 借阅控制器
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 借阅图书
     */
    @PostMapping
    public Response<BorrowRecord> borrowBook(@RequestBody Map<String, Object> request) {
        try {
            Long bookId = Long.valueOf(request.get("bookId").toString());
            String phone = request.get("phone").toString();
            BorrowRecord record = borrowService.borrowBook(bookId, phone);
            return Response.success(record);
        } catch (Exception e) {
            return Response.fail("借阅失败: " + e.getMessage());
        }
    }

    /**
     * 归还图书
     */
    @PutMapping("/return")
    public Response<BorrowRecord> returnBook(@RequestBody Map<String, Object> request) {
        try {
            Long bookId = Long.valueOf(request.get("bookId").toString());
            String phone = request.get("phone").toString();
            BorrowRecord record = borrowService.returnBook(bookId, phone);
            return Response.success(record);
        } catch (Exception e) {
            return Response.fail("归还失败: " + e.getMessage());
        }
    }

    /**
     * 续借图书
     */
    @PutMapping("/renew")
    public Response<BorrowRecord> renewBook(@RequestBody Map<String, Object> request) {
        try {
            Long recordId = Long.valueOf(request.get("recordId").toString());
            BorrowRecord record = borrowService.renewBook(recordId);
            return Response.success(record);
        } catch (Exception e) {
            return Response.fail("续借失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有借阅记录
     */
    @GetMapping
    public Response<List<BorrowRecord>> getBorrowRecordList() {
        List<BorrowRecord> records = borrowService.findAll();
        return Response.success(records);
    }

    /**
     * 获取借阅记录详情
     */
    @GetMapping("/{id}")
    public Response<BorrowRecord> getBorrowRecordById(@PathVariable Long id) {
        Optional<BorrowRecord> record = borrowService.findById(id);
        return record.map(Response::success).orElse(Response.fail("借阅记录不存在"));
    }

    /**
     * 获取用户的借阅记录
     */
    @GetMapping("/user/{userId}")
    public Response<List<BorrowRecord>> getBorrowRecordsByUserId(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowService.findByUserId(userId);
        return Response.success(records);
    }

    /**
     * 获取用户的未还记录
     */
    @GetMapping("/user/{userId}/active")
    public Response<List<BorrowRecord>> getActiveBorrowRecordsByUserId(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowService.findActiveByUserId(userId);
        return Response.success(records);
    }

    /**
     * 获取图书的借阅记录
     */
    @GetMapping("/book/{bookId}")
    public Response<List<BorrowRecord>> getBorrowRecordsByBookId(@PathVariable Long bookId) {
        List<BorrowRecord> records = borrowService.findByBookId(bookId);
        return Response.success(records);
    }
}
