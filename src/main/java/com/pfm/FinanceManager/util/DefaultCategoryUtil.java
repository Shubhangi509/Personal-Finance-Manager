package com.pfm.FinanceManager.util;

import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Component
public class DefaultCategoryUtil {

    public List<Category> createDefaultCategories(User user) {
        log.info("Creating default categories for user ID {}", user.getId());

        // Default INCOME category
        Category salary = Category.builder()
                .name("Salary")
                .type(TransactionType.INCOME)
                .isCustom(false)
                .user(user)
                .build();

        // Default EXPENSE categories
        List<Category> defaultExpenses = List.of(
                Category.builder().name("Food").type(TransactionType.EXPENSE).isCustom(false).user(user).build(),
                Category.builder().name("Rent").type(TransactionType.EXPENSE).isCustom(false).user(user).build(),
                Category.builder().name("Transportation").type(TransactionType.EXPENSE).isCustom(false).user(user).build(),
                Category.builder().name("Entertainment").type(TransactionType.EXPENSE).isCustom(false).user(user).build(),
                Category.builder().name("Healthcare").type(TransactionType.EXPENSE).isCustom(false).user(user).build(),
                Category.builder().name("Utilities").type(TransactionType.EXPENSE).isCustom(false).user(user).build()
        );

        List<Category> allDefaults = new ArrayList<>();
        allDefaults.add(salary);
        allDefaults.addAll(defaultExpenses);

        return allDefaults;
    }
}
