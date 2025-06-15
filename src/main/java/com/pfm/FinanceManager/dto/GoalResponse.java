package com.pfm.FinanceManager.dto;

import com.pfm.FinanceManager.entity.SavingsGoal.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GoalResponse {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String goalName;

    @Getter
    @Setter
    private BigDecimal targetAmount;

    @Getter
    @Setter
    private LocalDate targetDate;

    @Getter
    @Setter
    private LocalDate startDate;

    @Getter
    @Setter
    private BigDecimal currentProgress;

    @Getter
    @Setter
    private double progressPercentage;

    @Getter
    @Setter
    private BigDecimal remainingAmount;

    @Getter
    @Setter
    private GoalStatus status;
}
