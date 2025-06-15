package com.pfm.FinanceManager.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateGoalRequest {
    @Getter
    @Setter
    @NotBlank
    private String goalName;

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

    @Getter
    @Setter
    @PastOrPresent
    private LocalDate startDate;
}
