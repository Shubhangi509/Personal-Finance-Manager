package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.CreateGoalRequest;
import com.pfm.FinanceManager.dto.GoalResponse;
import com.pfm.FinanceManager.dto.UpdateGoalRequest;
import com.pfm.FinanceManager.entity.SavingsGoal;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.SavingsGoalRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceImplTest {

    @Mock
    private SavingsGoalRepository goalRepo;

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private SessionUtil sessionUtil;

    @InjectMocks
    private SavingsGoalServiceImpl savingsGoalService;

    private User testUser;
    private SavingsGoal testGoal;
    private CreateGoalRequest createRequest;
    private UpdateGoalRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");

        testGoal = new SavingsGoal();
        testGoal.setId(1L);
        testGoal.setGoalName("Test Goal");
        testGoal.setTargetAmount(new BigDecimal("1000.00"));
        testGoal.setTargetDate(LocalDate.now().plusMonths(6));
        testGoal.setStartDate(LocalDate.now());
        testGoal.setUser(testUser);
        testGoal.setStatus(SavingsGoal.GoalStatus.IN_PROGRESS);

        createRequest = new CreateGoalRequest();
        createRequest.setGoalName("New Goal");
        createRequest.setTargetAmount(new BigDecimal("2000.00"));
        createRequest.setTargetDate(LocalDate.now().plusMonths(12));
        createRequest.setStartDate(LocalDate.now());

        updateRequest = new UpdateGoalRequest();
        updateRequest.setTargetAmount(new BigDecimal("1500.00"));
        updateRequest.setTargetDate(LocalDate.now().plusMonths(9));
    }

    @Test
    void createGoal_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        SavingsGoal createdGoal = new SavingsGoal();
        createdGoal.setId(2L);
        createdGoal.setGoalName(createRequest.getGoalName());
        createdGoal.setTargetAmount(createRequest.getTargetAmount());
        createdGoal.setTargetDate(createRequest.getTargetDate());
        createdGoal.setStartDate(createRequest.getStartDate());
        createdGoal.setUser(testUser);
        createdGoal.setStatus(SavingsGoal.GoalStatus.IN_PROGRESS);
        when(goalRepo.save(any(SavingsGoal.class))).thenReturn(createdGoal);

        SavingsGoal result = savingsGoalService.createGoal(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getGoalName(), result.getGoalName());
        assertEquals(createRequest.getTargetAmount(), result.getTargetAmount());
        assertEquals(createRequest.getTargetDate(), result.getTargetDate());
        assertEquals(createRequest.getStartDate(), result.getStartDate());
        assertEquals(SavingsGoal.GoalStatus.IN_PROGRESS, result.getStatus());
        assertEquals(testUser, result.getUser());

        verify(goalRepo).save(any(SavingsGoal.class));
    }

    @Test
    void getAllGoals_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(goalRepo.findByUser(testUser)).thenReturn(Arrays.asList(testGoal));

        List<GoalResponse> results = savingsGoalService.getAllGoals();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testGoal.getGoalName(), results.get(0).getGoalName());
        assertEquals(testGoal.getTargetAmount(), results.get(0).getTargetAmount());
    }

    @Test
    void getGoalById_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(goalRepo.findById(1L)).thenReturn(Optional.of(testGoal));

        GoalResponse result = savingsGoalService.getGoalById(1L);

        assertNotNull(result);
        assertEquals(testGoal.getGoalName(), result.getGoalName());
        assertEquals(testGoal.getTargetAmount(), result.getTargetAmount());
    }

    @Test
    void getGoalById_NotFound() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(goalRepo.findById(1L)).thenReturn(Optional.empty());

        GoalResponse result = savingsGoalService.getGoalById(1L);
        assertNull(result);
    }

    @Test
    void updateGoal_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(goalRepo.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepo.save(any(SavingsGoal.class))).thenReturn(testGoal);

        SavingsGoal result = savingsGoalService.updateGoal(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getTargetAmount(), result.getTargetAmount());
        assertEquals(updateRequest.getTargetDate(), result.getTargetDate());
        verify(goalRepo).save(any(SavingsGoal.class));
    }

    @Test
    void deleteGoal_Success() {
        when(sessionUtil.getSessionUser()).thenReturn(testUser);
        when(goalRepo.findById(1L)).thenReturn(Optional.of(testGoal));
        doNothing().when(goalRepo).delete(testGoal);

        String result = savingsGoalService.deleteGoal(1L);

        assertEquals("Goal deleted successfully", result);
        verify(goalRepo).delete(testGoal);
    }
} 