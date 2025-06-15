package com.pfm.FinanceManager.controller;

import com.pfm.FinanceManager.dto.CategoryRequest;
import com.pfm.FinanceManager.dto.CategoryResponse;
import com.pfm.FinanceManager.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller handling category management operations including creation, retrieval,
 * updating, and deletion of transaction categories. All endpoints are prefixed with "/api/categories".
 * Supports both default and custom categories.
 *
 * @author FinanceManager Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new custom category.
     * Custom category names must be unique per user.
     *
     * @param request The category details including name, type, and description
     * @return CategoryResponse containing the created category details
     * @throws RuntimeException if a category with the same name already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody @Valid CategoryRequest request) {
        return categoryService.create(request);
    }

    /**
     * Retrieves all categories for the current user, including both default and custom categories.
     * If no categories exist, default categories are automatically created.
     * Default categories cannot be modified or deleted.
     *
     * @return List of CategoryResponse objects
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    /**
     * Updates an existing custom category.
     * Default categories cannot be modified.
     *
     * @param id The ID of the category to update
     * @param request The updated category details
     * @return CategoryResponse containing the updated category details
     * @throws RuntimeException if the category doesn't exist or is a default category
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse update(@PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
        return categoryService.update(id, request);
    }

    /**
     * Deletes a custom category.
     * Default categories cannot be deleted.
     * Categories currently referenced by transactions cannot be deleted.
     *
     * @param name The name of the category to delete
     * @return ResponseEntity with success message
     * @throws RuntimeException if the category doesn't exist, is a default category, or is in use
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String name) {
        categoryService.delete(name);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
}