package com.pfm.FinanceManager.dto;

import com.pfm.FinanceManager.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class CategoryRequest {
    @Getter
    @Setter
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    @Getter
    @Setter
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @Getter
    @Setter
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
}