package com.pfm.FinanceManager.service;

import com.pfm.FinanceManager.dto.MonthlyReportResponse;
import com.pfm.FinanceManager.dto.YearlyReportResponse;

public interface ReportService {
    MonthlyReportResponse getMonthlyReport(int year, int month);
    YearlyReportResponse getYearlyReport(int year);
}