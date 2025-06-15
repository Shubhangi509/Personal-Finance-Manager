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
import com.pfm.FinanceManager.service.SavingsGoalService;
import com.pfm.FinanceManager.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository goalRepo;
    private final TransactionRepository transactionRepo;
    private final SessionUtil sessionUtil;

    @Override
    public SavingsGoal createGoal(CreateGoalRequest request) {
        User user = sessionUtil.getSessionUser();
        if (user == null) {
            log.warn("Goal creation attempted without active session");
            return null;
        }
        log.info("Creating goal for user ID {}", user.getId());
        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        LocalDate startDate = request.getStartDate();
        if(startDate == null) startDate = LocalDate.now();
        goal.setStartDate(startDate);
        goal.setUser(user);
        SavingsGoal savedGoal = goalRepo.save(goal);

        log.info("Created new goal '{}' for user ID {} with target amount {}", request.getGoalName(), user.getId(), request.getTargetAmount());
        return savedGoal;
    }

    @Override
    public List<GoalResponse> getAllGoals() {
        User user = sessionUtil.getSessionUser();
        if (user == null) {
            log.warn("Attempt to retrieve all goals without active session");
            return Collections.emptyList();
        }
        log.info("Fetching all goals for user ID {}", user.getId());
        return goalRepo.findByUser(user).stream()
                .map(goal -> mapToGoalResponse(goal, user))
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse getGoalById(Long id) {
        User user = sessionUtil.getSessionUser();
        SavingsGoal goal = goalRepo.findById(id).orElse(null);

        if (goal == null) {
            log.warn("Goal ID {} not found for user ID {}", id, user.getId());
            return null;
        }
        if (!goal.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized access attempt to goal ID {} by user ID {}", id, user.getId());
            return null;
        }

        log.info("Fetched goal ID {} for user ID {}", id, user.getId());
        return mapToGoalResponse(goal, user);
    }

    @Override
    public SavingsGoal updateGoal(Long id, UpdateGoalRequest request) {
        User user = sessionUtil.getSessionUser();
        if (user == null) {
            log.warn("Attempt to update goal ID {} without active session", id);
            return null;
        }

        log.info("Updating goal ID {} for user ID {}", id, user.getId());

        SavingsGoal goal = goalRepo.findById(id).orElse(null);
        if (goal == null) {
            log.warn("Cannot update: goal ID {} not found for user ID {}", id, user.getId());
            return null;
        }
        if (!goal.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized update attempt on goal ID {} by user ID {}", id, user.getId());
            return null;
        }

        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        SavingsGoal updatedGoal = goalRepo.save(goal);
        log.info("Updated goal ID {} for user ID {}", id, user.getId());
        return updatedGoal;
    }

    @Override
    public String deleteGoal(Long id) {
        User user = sessionUtil.getSessionUser();
        if (user == null) {
            log.warn("Attempt to delete goal ID {} without active session", id);
            throw new RuntimeException("Attempt to delete goal without active session");
        }

        log.info("Deleting goal ID {} for user ID {}", id, user.getId());

        SavingsGoal goal = goalRepo.findById(id).orElse(null);
        if (goal == null) {
            log.warn("Cannot delete: goal ID {} not found for user ID {}", id, user.getId());
            throw new RuntimeException("Cannot delete goal, user not found");
        }
        if (!goal.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized delete attempt on goal ID {} by user ID {}", id, user.getId());
            throw new RuntimeException("Unauthorized delete attempt on goal");
        }

        goalRepo.delete(goal);
        log.info("Deleted goal ID {} for user ID {}", id, user.getId());
        return "Goal deleted successfully";
    }

    private GoalResponse mapToGoalResponse(SavingsGoal goal, User user) {
        LocalDate startDate = goal.getStartDate();
        LocalDate targetDate = goal.getTargetDate();
        LocalDate currentDate = LocalDate.now();
        
        // Get transactions between start date and target date
        List<Transaction> transactions = transactionRepo.findByUserAndDateGreaterThanEqual(user, startDate)
                .stream()
                .filter(t -> !t.getDate().isAfter(targetDate))
                .collect(Collectors.toList());

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate current progress (ensure it's not negative)
        BigDecimal currentProgress = totalIncome.subtract(totalExpense).max(BigDecimal.ZERO);
        
        // Calculate remaining amount
        BigDecimal remaining = goal.getTargetAmount().subtract(currentProgress).max(BigDecimal.ZERO);
        
        // Calculate percentage (ensure it's not negative and capped at 100%)
        double percentage = goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0 ?
                currentProgress.multiply(BigDecimal.valueOf(100))
                        .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                        .min(BigDecimal.valueOf(100))
                        .doubleValue() : 0.0;

        // Determine goal status
        SavingsGoal.GoalStatus status;
        if (currentProgress.compareTo(goal.getTargetAmount()) >= 0) {
            status = SavingsGoal.GoalStatus.COMPLETED;
        } else if (currentDate.isAfter(targetDate)) {
            status = SavingsGoal.GoalStatus.OVERDUE;
        } else {
            status = SavingsGoal.GoalStatus.IN_PROGRESS;
        }

        // Update goal status if it has changed
        if (goal.getStatus() != status) {
            goal.setStatus(status);
            goalRepo.save(goal);
        }

        return new GoalResponse(
                goal.getId(), goal.getGoalName(), goal.getTargetAmount(), goal.getTargetDate(),
                goal.getStartDate(), currentProgress, percentage, remaining, status
        );
    }
}
