package com.pfm.FinanceManager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "savings_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String goalName;

    @Getter
    @Setter
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal targetAmount;

    @Future
    @Getter
    @Setter
    private LocalDate targetDate;

    @Getter
    @Setter
    private LocalDate startDate;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public enum GoalStatus {
        IN_PROGRESS,
        COMPLETED,
        OVERDUE
    }
}

