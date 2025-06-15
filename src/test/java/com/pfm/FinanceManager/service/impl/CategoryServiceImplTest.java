package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.CategoryRequest;
import com.pfm.FinanceManager.dto.CategoryResponse;
import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.CategoryRepository;
import com.pfm.FinanceManager.util.DefaultCategoryUtil;
import com.pfm.FinanceManager.util.SessionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private SessionUtil sessionUtil;

    @Mock
    private DefaultCategoryUtil defaultCategoryUtil;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category testCategory;
    private CategoryRequest createRequest;
    private CategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setType(TransactionType.EXPENSE);
        testCategory.setDescription("Food expenses");
        testCategory.setUser(testUser);
        testCategory.setDefault(false);

        createRequest = new CategoryRequest();
        createRequest.setName("New Category");
        createRequest.setType(TransactionType.EXPENSE);
        createRequest.setDescription("New category description");

        updateRequest = new CategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setType(TransactionType.EXPENSE);
        updateRequest.setDescription("Updated category description");
    }

    @Test
    void create_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("New Category", testUser)).thenReturn(Optional.empty());
        Category savedCategory = new Category();
        savedCategory.setId(2L);
        savedCategory.setName(createRequest.getName());
        savedCategory.setType(createRequest.getType());
        savedCategory.setDescription(createRequest.getDescription());
        savedCategory.setUser(testUser);
        savedCategory.setDefault(false);
        when(categoryRepo.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.create(createRequest);

        assertNotNull(response);
        assertEquals(createRequest.getName(), response.getName());
        assertEquals(createRequest.getType(), response.getType());
        assertEquals(createRequest.getDescription(), response.getDescription());
        assertFalse(response.isDefault());

        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void create_DuplicateName() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("New Category", testUser)).thenReturn(Optional.of(testCategory));

        assertThrows(RuntimeException.class, () -> categoryService.create(createRequest));
    }

    @Test
    void getAll_WithExistingCategories() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByUser(testUser)).thenReturn(Arrays.asList(testCategory));

        List<CategoryResponse> responses = categoryService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testCategory.getName(), responses.get(0).getName());
        assertEquals(testCategory.getType(), responses.get(0).getType());
    }

    @Test
    void getAll_NoCategories() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByUser(testUser)).thenReturn(List.of());
        when(defaultCategoryUtil.createDefaultCategories(testUser)).thenReturn(Arrays.asList(testCategory));
        when(categoryRepo.saveAll(any())).thenReturn(Arrays.asList(testCategory));

        List<CategoryResponse> responses = categoryService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testCategory.getName(), responses.get(0).getName());
        assertEquals(testCategory.getType(), responses.get(0).getType());

        verify(defaultCategoryUtil).createDefaultCategories(testUser);
        verify(categoryRepo).saveAll(any());
    }

    @Test
    void update_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepo.findByNameAndUser("Updated Category", testUser)).thenReturn(Optional.empty());
        when(categoryRepo.save(any(Category.class))).thenReturn(testCategory);

        CategoryResponse response = categoryService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getType(), response.getType());
        assertEquals(updateRequest.getDescription(), response.getDescription());

        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void update_CategoryNotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.update(1L, updateRequest));
    }

    @Test
    void update_UnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        testCategory.setUser(otherUser);

        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(testCategory));

        assertThrows(RuntimeException.class, () -> categoryService.update(1L, updateRequest));
    }

    @Test
    void update_DefaultCategory() {
        testCategory.setDefault(true);

        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(testCategory));

        assertThrows(RuntimeException.class, () -> categoryService.update(1L, updateRequest));
    }

    @Test
    void delete_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.of(testCategory));
        when(categoryRepo.isCategoryInUse(testCategory)).thenReturn(false);
        doNothing().when(categoryRepo).deleteById(1L);

        categoryService.delete("Food");

        verify(categoryRepo).deleteById(1L);
    }

    @Test
    void delete_CategoryNotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.delete("Food"));
    }

    @Test
    void delete_DefaultCategory() {
        testCategory.setDefault(true);

        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.of(testCategory));

        assertThrows(RuntimeException.class, () -> categoryService.delete("Food"));
    }

    @Test
    void delete_CategoryInUse() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.of(testCategory));
        when(categoryRepo.isCategoryInUse(testCategory)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> categoryService.delete("Food"));
    }
} 