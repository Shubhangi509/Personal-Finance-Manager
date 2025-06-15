package com.pfm.FinanceManager.service;

import com.pfm.FinanceManager.dto.TransactionRequest;
import com.pfm.FinanceManager.dto.TransactionResponse;
import com.pfm.FinanceManager.dto.TransactionUpdateRequest;
import com.pfm.FinanceManager.entity.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    List<TransactionResponse> getAll();
    List<TransactionResponse> getAllSortedByDate();
    List<TransactionResponse> getByDateRange(LocalDate start, LocalDate end);
    List<TransactionResponse> getByType(TransactionType type);
    List<TransactionResponse> getByCategory(Long categoryId);
    List<TransactionResponse> getByDateRangeAndType(LocalDate start, LocalDate end, TransactionType type);
    TransactionResponse update(Long id, TransactionUpdateRequest request);
    void delete(Long id);
}
