package com.pfm.FinanceManager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateGoalRequest {
    @Getter
    @Setter
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal targetAmount;

    @Getter
    @Setter
    @NotNull
    @Future
    private LocalDate targetDate;
}
