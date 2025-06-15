package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.MonthlyReportResponse;
import com.pfm.FinanceManager.dto.YearlyReportResponse;
import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private SessionUtil sessionUtil;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;
    private Transaction expenseTransaction;
    private Transaction incomeTransaction;
    private Category foodCategory;
    private Category salaryCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");

        foodCategory = new Category();
        foodCategory.setId(1L);
        foodCategory.setName("Food");
        foodCategory.setType(TransactionType.EXPENSE);
        foodCategory.setUser(testUser);

        salaryCategory = new Category();
        salaryCategory.setId(2L);
        salaryCategory.setName("Salary");
        salaryCategory.setType(TransactionType.INCOME);
        salaryCategory.setUser(testUser);

        expenseTransaction = new Transaction();
        expenseTransaction.setId(1L);
        expenseTransaction.setAmount(new BigDecimal("100.00"));
        expenseTransaction.setType(TransactionType.EXPENSE);
        expenseTransaction.setCategory(foodCategory);
        expenseTransaction.setUser(testUser);
        expenseTransaction.setDate(LocalDate.now());

        incomeTransaction = new Transaction();
        incomeTransaction.setId(2L);
        incomeTransaction.setAmount(new BigDecimal("1000.00"));
        incomeTransaction.setType(TransactionType.INCOME);
        incomeTransaction.setCategory(salaryCategory);
        incomeTransaction.setUser(testUser);
        incomeTransaction.setDate(LocalDate.now());
    }

    @Test
    void getMonthlyReport_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findAllByUserAndDateBetween(any(), any(), any()))
                .thenReturn(Arrays.asList(expenseTransaction, incomeTransaction));

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 3);

        assertNotNull(response);
        assertEquals(3, response.getMonth());
        assertEquals(2024, response.getYear());
        assertEquals(new BigDecimal("1000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("100.00"), response.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("900.00"), response.getNetSavings());
    }

    @Test
    void getMonthlyReport_NoTransactions() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findAllByUserAndDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        MonthlyReportResponse response = reportService.getMonthlyReport(2024, 3);

        assertNotNull(response);
        assertEquals(3, response.getMonth());
        assertEquals(2024, response.getYear());
        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getNetSavings());
    }

    @Test
    void getYearlyReport_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findAllByUserAndDateBetween(any(), any(), any()))
                .thenReturn(Arrays.asList(expenseTransaction, incomeTransaction));

        YearlyReportResponse response = reportService.getYearlyReport(2024);

        assertNotNull(response);
        assertEquals(2024, response.getYear());
        assertEquals(new BigDecimal("1000.00"), response.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("100.00"), response.getTotalExpenses().get("Food"));
        assertEquals(new BigDecimal("900.00"), response.getNetSavings());
    }

    @Test
    void getYearlyReport_NoTransactions() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(transactionRepo.findAllByUserAndDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        YearlyReportResponse response = reportService.getYearlyReport(2024);

        assertNotNull(response);
        assertEquals(2024, response.getYear());
        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getNetSavings());
    }
} 