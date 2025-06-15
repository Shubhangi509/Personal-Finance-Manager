package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.TransactionRequest;
import com.pfm.FinanceManager.dto.TransactionResponse;
import com.pfm.FinanceManager.dto.TransactionUpdateRequest;
import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.CategoryRepository;
import com.pfm.FinanceManager.repository.TransactionRepository;
import com.pfm.FinanceManager.util.SessionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private SessionUtil sessionUtil;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;
    private TransactionRequest createRequest;
    private TransactionUpdateRequest updateRequest;

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
        testCategory.setUser(testUser);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType(TransactionType.EXPENSE);
        testTransaction.setCategory(testCategory);
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Grocery shopping");
        testTransaction.setUser(testUser);

        createRequest = new TransactionRequest();
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setDate(LocalDate.now());
        createRequest.setCategory("Food");
        createRequest.setDescription("Grocery shopping");

        updateRequest = new TransactionUpdateRequest();
        updateRequest.setAmount(new BigDecimal("150.00"));
        updateRequest.setDescription("Updated grocery shopping");
    }

    @Test
    void create_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.of(testCategory));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.create(createRequest);

        assertNotNull(response);
        assertEquals(testTransaction.getAmount(), response.getAmount());
        assertEquals(testTransaction.getType(), response.getType());
        assertEquals(testTransaction.getCategory().getName(), response.getCategory());
        assertEquals(testTransaction.getDescription(), response.getDescription());
        assertEquals(testTransaction.getDate(), response.getDate());

        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void create_CategoryNotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findByNameAndUser("Food", testUser)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.create(createRequest));
    }

    @Test
    void getAll_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findByUser(testUser)).thenReturn(Arrays.asList(testTransaction));

        List<TransactionResponse> responses = transactionService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTransaction.getAmount(), responses.get(0).getAmount());
        assertEquals(testTransaction.getType(), responses.get(0).getType());
    }

    @Test
    void getAllSortedByDate_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findByUserOrderByDateDesc(testUser)).thenReturn(Arrays.asList(testTransaction));

        List<TransactionResponse> responses = transactionService.getAllSortedByDate();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTransaction.getAmount(), responses.get(0).getAmount());
        assertEquals(testTransaction.getType(), responses.get(0).getType());
    }

    @Test
    void getByDateRange_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findAllByUserAndDateBetween(any(), any(), any()))
                .thenReturn(Arrays.asList(testTransaction));

        List<TransactionResponse> responses = transactionService.getByDateRange(
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTransaction.getAmount(), responses.get(0).getAmount());
        assertEquals(testTransaction.getType(), responses.get(0).getType());
    }

    @Test
    void getByType_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findByUserAndType(testUser, TransactionType.EXPENSE))
                .thenReturn(Arrays.asList(testTransaction));

        List<TransactionResponse> responses = transactionService.getByType(TransactionType.EXPENSE);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTransaction.getAmount(), responses.get(0).getAmount());
        assertEquals(testTransaction.getType(), responses.get(0).getType());
    }

    @Test
    void getByCategory_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepo.findByUserAndCategory(testUser, testCategory))
                .thenReturn(Arrays.asList(testTransaction));

        List<TransactionResponse> responses = transactionService.getByCategory("");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTransaction.getAmount(), responses.get(0).getAmount());
        assertEquals(testTransaction.getType(), responses.get(0).getType());
    }

    @Test
    void update_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getAmount(), response.getAmount());
        assertEquals(updateRequest.getDescription(), response.getDescription());
        verify(transactionRepo).save(any(Transaction.class));
    }

    @Test
    void update_TransactionNotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.update(1L, updateRequest));
    }

    @Test
    void update_UnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        testTransaction.setUser(otherUser);

        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(testTransaction));

        assertThrows(RuntimeException.class, () -> transactionService.update(1L, updateRequest));
    }

    @Test
    void delete_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(testTransaction));
        doNothing().when(transactionRepo).deleteById(1L);

        transactionService.delete(1L);

        verify(transactionRepo).deleteById(1L);
    }

    @Test
    void delete_TransactionNotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.delete(1L));
    }

    @Test
    void delete_UnauthorizedUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        testTransaction.setUser(otherUser);

        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(testTransaction));

        assertThrows(RuntimeException.class, () -> transactionService.delete(1L));
    }
} 