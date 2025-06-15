package com.pfm.FinanceManager.service;

import com.pfm.FinanceManager.dto.RegisterRequest;
import com.pfm.FinanceManager.dto.LoginRequest;
import com.pfm.FinanceManager.dto.ApiResponse;

public interface AuthService {
    ApiResponse register(RegisterRequest request);
    ApiResponse login(LoginRequest request);
    ApiResponse logout();
}
