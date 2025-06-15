package com.pfm.FinanceManager.service;

import com.pfm.FinanceManager.dto.CategoryRequest;
import com.pfm.FinanceManager.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> getAll();
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(String name);
}
