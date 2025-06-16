package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.CategoryRequest;
import com.pfm.FinanceManager.dto.CategoryResponse;
import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.CategoryRepository;
import com.pfm.FinanceManager.service.CategoryService;
import com.pfm.FinanceManager.util.DefaultCategoryUtil;
import com.pfm.FinanceManager.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final SessionUtil sessionUtil;
    private final DefaultCategoryUtil defaultCategoryUtil;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        User user = sessionUtil.getSessionUser();
        log.info("Creating category '{}' for user ID {}", request.getName(), user.getId());

        // Check if category name already exists for this user
        if (categoryRepo.findByNameAndUser(request.getName(), user).isPresent()) {
            log.warn("Category name '{}' already exists for user ID {}", request.getName(), user.getId());
            throw new RuntimeException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .isCustom(true)
                .user(user)
                .build();
        Category saved = categoryRepo.save(category);
        log.info("Category created successfully with ID: {}", saved.getId());
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching all categories for user ID {}", user.getId());

        // Get user's categories (both default and custom)
        List<Category> categories = categoryRepo.findByUser(user);
        
        // If user has no categories, create default ones
        if (categories.isEmpty()) {
            log.info("No categories found for user ID {}, creating default categories", user.getId());
            categories = categoryRepo.saveAll(defaultCategoryUtil.createDefaultCategories(user));
        }

        List<CategoryResponse> responses = categories.stream()
                .map(this::mapToDto)
                .toList();

        log.info("Found {} categories for user ID {}", responses.size(), user.getId());
        return responses;
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        User user = sessionUtil.getSessionUser();
        log.info("Updating category ID {} for user ID {}", id, user.getId());

        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed - category not found with ID: {}", id);
                    return new RuntimeException("Category not found");
                });

        // Validate category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} attempted to update category ID {} not belonging to them", user.getId(), id);
            throw new RuntimeException("Category not accessible to user");
        }

        // Prevent modification of default categories
        if (!category.isCustom()) {
            log.warn("Attempted to update default category ID {}", id);
            throw new RuntimeException("Cannot modify default categories");
        }

        // Check if new name conflicts with existing category
        categoryRepo.findByNameAndUser(request.getName(), user)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(id)) {
                        log.warn("Category name '{}' already exists for user ID {}", request.getName(), user.getId());
                        throw new RuntimeException("Category name already exists");
                    }
                });

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setType(request.getType());

        Category updated = categoryRepo.save(category);
        log.info("Category ID {} updated successfully", updated.getId());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void delete(String name) {
        User user = sessionUtil.getSessionUser();
        log.info("Attempting to delete category '{}' for user ID {}", name, user.getId());

        Category category = categoryRepo.findByNameAndUser(name, user)
                .orElseThrow(() -> {
                    log.error("Delete failed - category '{}' not found for user ID {}", name, user.getId());
                    return new RuntimeException("Category not found");
                });

        // Prevent deletion of default categories
        if (!category.isCustom()) {
            log.warn("Attempted to delete default category '{}'", name);
            throw new RuntimeException("Cannot delete default categories");
        }

        // Check if category is in use
        if (categoryRepo.isCategoryInUse(category)) {
            log.warn("Cannot delete category '{}' - it is referenced by transactions", name);
            throw new RuntimeException("Cannot delete category that is in use");
        }

        categoryRepo.deleteById(category.getId());
        log.info("Category '{}' deleted successfully", name);
    }

    private CategoryResponse mapToDto(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType())
                .isCustom(category.isCustom())
                .build();
    }
}