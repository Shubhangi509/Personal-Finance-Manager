package com.pfm.FinanceManager.repository;

import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    List<Category> findByUserAndType(User user, TransactionType type);
    List<Category> findByIsDefaultTrue();
    Optional<Category> findByNameAndUser(String name, User user);
    
    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.category = ?1")
    boolean isCategoryInUse(Category category);
}
