package com.pfm.FinanceManager.service;

import com.pfm.FinanceManager.dto.CreateGoalRequest;
import com.pfm.FinanceManager.dto.GoalResponse;
import com.pfm.FinanceManager.dto.UpdateGoalRequest;
import com.pfm.FinanceManager.entity.SavingsGoal;
import com.pfm.FinanceManager.entity.User;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoal createGoal(CreateGoalRequest request);
    List<GoalResponse> getAllGoals();
    GoalResponse getGoalById(Long id);
    SavingsGoal updateGoal(Long id, UpdateGoalRequest request);
    String deleteGoal(Long id);
}

