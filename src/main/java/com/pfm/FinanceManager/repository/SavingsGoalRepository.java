package com.pfm.FinanceManager.repository;

import com.pfm.FinanceManager.entity.SavingsGoal;
import com.pfm.FinanceManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}
