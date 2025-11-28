package com.booklending.controller;

import com.booklending.model.Category;
import com.booklending.service.CategoryService;
import com.booklending.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Response<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.findAll();
        return Response.success(categories);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Response<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(Response::success).orElse(Response.fail("分类不存在"));
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Response<Category> addCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.save(category);
            return Response.success(savedCategory);
        } catch (Exception e) {
            return Response.fail("新增分类失败: " + e.getMessage());
        }
    }

    /**
     * 修改分类
     */
    @PutMapping("/{id}")
    public Response<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            // 确保ID一致
            category.setId(id);
            Category updatedCategory = categoryService.save(category);
            return Response.success(updatedCategory);
        } catch (Exception e) {
            return Response.fail("修改分类失败: " + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Response<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return Response.success("删除成功");
        } catch (Exception e) {
            return Response.fail("删除分类失败: " + e.getMessage());
        }
    }
}