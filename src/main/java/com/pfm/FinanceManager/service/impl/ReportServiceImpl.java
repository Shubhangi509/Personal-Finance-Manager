package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.MonthlyReportResponse;
import com.pfm.FinanceManager.dto.YearlyReportResponse;
import com.pfm.FinanceManager.entity.Transaction;
import com.pfm.FinanceManager.entity.TransactionType;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.TransactionRepository;
import com.pfm.FinanceManager.service.ReportService;
import com.pfm.FinanceManager.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final SessionUtil sessionUtil;

    @Override
    public MonthlyReportResponse getMonthlyReport(int year, int month) {
        User user = sessionUtil.getSessionUser();
        log.info("Generating monthly report for user ID {} for {}/{}", user.getId(), month, year);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findAllByUserAndDateBetween(user, start, end);
        log.info("Found {} transactions for the month", transactions.size());

        Map<String, BigDecimal> income = new HashMap<>();
        Map<String, BigDecimal> expenses = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            String categoryName = tx.getCategory().getName();
            BigDecimal amount = tx.getAmount();

            if (tx.getType() == TransactionType.INCOME) {
                income.put(categoryName, income.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalIncome = totalIncome.add(amount);
            } else {
                expenses.put(categoryName, expenses.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalExpenses = totalExpenses.add(amount);
            }
        }

        log.info("Monthly income: {}, expenses: {}, net savings: {}", totalIncome, totalExpenses, totalIncome.subtract(totalExpenses));
        return new MonthlyReportResponse(month, year, income, expenses, totalIncome.subtract(totalExpenses));
    }

    @Override
    public YearlyReportResponse getYearlyReport(int year) {
        User user = sessionUtil.getSessionUser();
        log.info("Generating yearly report for user ID {} for {}", user.getId(), year);

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findAllByUserAndDateBetween(user, start, end);
        log.info("Found {} transactions for the year", transactions.size());

        Map<String, BigDecimal> income = new HashMap<>();
        Map<String, BigDecimal> expenses = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            String categoryName = tx.getCategory().getName();
            BigDecimal amount = tx.getAmount();

            if (tx.getType() == TransactionType.INCOME) {
                income.put(categoryName, income.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalIncome = totalIncome.add(amount);
            } else {
                expenses.put(categoryName, expenses.getOrDefault(categoryName, BigDecimal.ZERO).add(amount));
                totalExpenses = totalExpenses.add(amount);
            }
        }

        log.info("Yearly income: {}, expenses: {}, net savings: {}", totalIncome, totalExpenses, totalIncome.subtract(totalExpenses));
        return new YearlyReportResponse(year, income, expenses, totalIncome.subtract(totalExpenses));
    }
}

