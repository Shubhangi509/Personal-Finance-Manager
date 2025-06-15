package com.pfm.FinanceManager.repository;

import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndDateGreaterThanEqual(User user, LocalDate date);
    List<Transaction> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndCategory(User user, Category category);
    List<Transaction> findByUserAndType(User user, TransactionType type);
    List<Transaction> findByUserAndDateBetweenAndType(User user, LocalDate start, LocalDate end, TransactionType type);

}

