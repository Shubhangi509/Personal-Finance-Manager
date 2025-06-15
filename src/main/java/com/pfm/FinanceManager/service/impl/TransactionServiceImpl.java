package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.TransactionRequest;
import com.pfm.FinanceManager.dto.TransactionResponse;
import com.pfm.FinanceManager.dto.TransactionUpdateRequest;
import com.pfm.FinanceManager.entity.Category;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.CategoryRepository;
import com.pfm.FinanceManager.repository.TransactionRepository;
import com.pfm.FinanceManager.service.TransactionService;
import com.pfm.FinanceManager.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;
    private final SessionUtil sessionUtil;

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = sessionUtil.getSessionUser();
        log.info("Creating transaction for user ID {}", user.getId());

        // Find category by name for the current user
        Category category = categoryRepo.findByNameAndUser(request.getCategory(), user)
                .orElseThrow(() -> {
                    log.warn("Category '{}' not found for user ID {}", request.getCategory(), user.getId());
                    return new RuntimeException("Category not found");
                });

        // Create transaction with category's type
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .type(category.getType()) // Set type from category
                .category(category)
                .user(user)
                .build();

        // Save and verify the transaction
        Transaction saved = transactionRepo.save(transaction);
        log.info("Transaction created with ID {} and type {}", saved.getId(), saved.getType());

        // Map to DTO and verify type is set
        TransactionResponse response = mapToDto(saved);
        if (response.getType() == null) {
            log.error("Transaction type is null after mapping for transaction ID {}", saved.getId());
            throw new RuntimeException("Failed to set transaction type");
        }

        return response;
    }

    @Override
    public List<TransactionResponse> getAll() {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching all transactions for user ID {}", user.getId());

        List<Transaction> transactions = transactionRepo.findByUser(user);
        log.info("Found {} transactions for user ID {}", transactions.size(), user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransactionResponse> getAllSortedByDate() {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching all transactions sorted by date for user ID {}", user.getId());

        List<Transaction> transactions = transactionRepo.findByUserOrderByDateDesc(user);
        log.info("Found {} transactions for user ID {}", transactions.size(), user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransactionResponse> getByDateRange(LocalDate start, LocalDate end) {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching transactions between {} and {} for user ID {}", start, end, user.getId());

        List<Transaction> transactions = transactionRepo.findAllByUserAndDateBetween(user, start, end);
        log.info("Found {} transactions in date range for user ID {}", transactions.size(), user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransactionResponse> getByType(TransactionType type) {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching {} transactions for user ID {}", type, user.getId());

        List<Transaction> transactions = transactionRepo.findByUserAndType(user, type);
        log.info("Found {} {} transactions for user ID {}", transactions.size(), type, user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransactionResponse> getByCategory(Long categoryId) {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching transactions for category ID {} and user ID {}", categoryId, user.getId());

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category ID {} not found", categoryId);
                    return new RuntimeException("Category not found");
                });

        // Validate category belongs to user
        if (!category.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} attempted to access category ID {} not belonging to them", user.getId(), category.getId());
            throw new RuntimeException("Category not accessible to user");
        }

        List<Transaction> transactions = transactionRepo.findByUserAndCategory(user, category);
        log.info("Found {} transactions for category ID {} and user ID {}", transactions.size(), categoryId, user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TransactionResponse> getByDateRangeAndType(LocalDate start, LocalDate end, TransactionType type) {
        User user = sessionUtil.getSessionUser();
        log.info("Fetching {} transactions between {} and {} for user ID {}", type, start, end, user.getId());

        List<Transaction> transactions = transactionRepo.findByUserAndDateBetweenAndType(user, start, end, type);
        log.info("Found {} {} transactions in date range for user ID {}", transactions.size(), type, user.getId());

        return transactions.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse update(Long id, TransactionUpdateRequest request) {
        User user = sessionUtil.getSessionUser();
        log.info("Updating transaction ID {} for user ID {}", id, user.getId());

        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transaction ID {} not found", id);
                    return new RuntimeException("Transaction not found");
                });

        // Validate transaction belongs to user
        if (!transaction.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} attempted to update transaction ID {} not belonging to them", user.getId(), id);
            throw new RuntimeException("Transaction not accessible to user");
        }

        // Update only the fields that are provided in the request
        if (request.getAmount() != null) {
        transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
        transaction.setDescription(request.getDescription());
        }

        // Handle category update if provided
        if (request.getCategory() != null) {
            Category category = categoryRepo.findByNameAndUser(request.getCategory(), user)
                    .orElseThrow(() -> {
                        log.warn("Category '{}' not found for user ID {}", request.getCategory(), user.getId());
                        return new RuntimeException("Category not found");
                    });
        transaction.setCategory(category);
            transaction.setType(category.getType()); // Update type based on new category
        }

        // Note: Date is not allowed to be updated as per requirements
        if (request.getDate() != null) {
            log.warn("Attempted to update date for transaction ID {}, which is not allowed", id);
            throw new RuntimeException("Transaction date cannot be modified");
        }

        Transaction updated = transactionRepo.save(transaction);
        log.info("Transaction ID {} updated with type {}", updated.getId(), updated.getType());

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = sessionUtil.getSessionUser();
        log.info("Attempting to delete transaction ID {} for user ID {}", id, user.getId());

        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> {
            log.warn("Transaction ID {} not found for deletion", id);
                    return new RuntimeException("Transaction not found");
                });

        // Validate transaction belongs to user
        if (!transaction.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} attempted to delete transaction ID {} not belonging to them", user.getId(), id);
            throw new RuntimeException("Transaction not accessible to user");
        }

        transactionRepo.deleteById(id);
        log.info("Transaction ID {} deleted", id);
    }

    private TransactionResponse mapToDto(Transaction t) {
        if (t.getType() == null) {
            log.error("Transaction type is null for transaction ID {}", t.getId());
            throw new RuntimeException("Transaction type is null");
        }

        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .date(t.getDate())
                .description(t.getDescription())
                .type(t.getType())
                .category(t.getCategory().getName())
                .build();
    }
}

