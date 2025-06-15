package com.pfm.FinanceManager.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class YearlyReportResponse {
    @Getter
    @Setter
    private int year;

    @Getter
    @Setter
    private Map<String, BigDecimal> totalIncome;

    @Getter
    @Setter
    private Map<String, BigDecimal> totalExpenses;

    @Getter
    @Setter
    private BigDecimal netSavings;
}
