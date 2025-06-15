package com.pfm.FinanceManager.controller;

import com.pfm.FinanceManager.dto.MonthlyReportResponse;
import com.pfm.FinanceManager.dto.YearlyReportResponse;
import com.pfm.FinanceManager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling financial reports generation including monthly and yearly reports.
 * All endpoints are prefixed with "/api/reports". Provides comprehensive financial
 * analysis and trend tracking.
 *
 * @author FinanceManager Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generates a monthly financial report for the specified year and month.
     * The report includes:
     * - Total income and expenses
     * - Category-wise breakdown
     * - Net savings
     * - Trend analysis compared to previous month
     *
     * @param year The year for the report (1900-2100)
     * @param month The month for the report (1-12)
     * @return ResponseEntity containing the monthly report data
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(@PathVariable int year, @PathVariable int month) {
        MonthlyReportResponse response = reportService.getMonthlyReport(year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a yearly financial report for the specified year.
     * The report includes:
     * - Total income and expenses for the year
     * - Monthly breakdown
     * - Category-wise aggregation
     * - Net savings for the year
     *
     * @param year The year for the report (1900-2100)
     * @return ResponseEntity containing the yearly report data
     * @throws BadRequestException if the year is invalid
     * @throws UnauthorizedException if no active session exists
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(@PathVariable int year) {
        YearlyReportResponse response = reportService.getYearlyReport(year);
        return ResponseEntity.ok(response);
    }
}
