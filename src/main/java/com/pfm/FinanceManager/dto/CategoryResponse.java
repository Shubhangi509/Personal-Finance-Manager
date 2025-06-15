package com.pfm.FinanceManager.dto;

import com.pfm.FinanceManager.entity.TransactionType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private TransactionType type;

    @Getter
    @Setter
    private boolean isDefault;
}
