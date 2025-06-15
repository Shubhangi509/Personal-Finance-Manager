package com.pfm.FinanceManager.dto;

import com.pfm.FinanceManager.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private LocalDate date;

    @Getter
    @Setter
    private String category;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private TransactionType type;
}
