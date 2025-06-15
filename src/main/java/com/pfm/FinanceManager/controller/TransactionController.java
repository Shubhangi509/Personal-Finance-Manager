package com.pfm.FinanceManager.controller;

import com.pfm.FinanceManager.dto.TransactionRequest;
import com.pfm.FinanceManager.dto.TransactionResponse;
import com.pfm.FinanceManager.dto.TransactionUpdateRequest;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestBody @Valid TransactionRequest request) {
        return transactionService.create(request);
    }

    @GetMapping
    public Map<String, List<TransactionResponse>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId) {
        List<TransactionResponse> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionService.getByDateRange(startDate, endDate);
        } else if (categoryId != null) {
            transactions = transactionService.getByCategory(categoryId);
        } else {
            transactions = transactionService.getAllSortedByDate();
        }
        return Map.of("transactions", transactions);
    }

    @GetMapping("/filter/type/{type}")
    public List<TransactionResponse> getByType(@PathVariable TransactionType type) {
        return transactionService.getByType(type);
    }

    @GetMapping("/filter/date-type")
    public List<TransactionResponse> getByDateRangeAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam TransactionType type) {
        return transactionService.getByDateRangeAndType(start, end, type);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id, @RequestBody @Valid TransactionUpdateRequest request) {
        return transactionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }
}
