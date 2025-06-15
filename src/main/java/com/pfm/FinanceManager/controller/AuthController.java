package com.pfm.FinanceManager.controller;

import com.pfm.FinanceManager.dto.*;
import com.pfm.FinanceManager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user authentication operations including registration, login, and logout.
 * All endpoints are prefixed with "/api/auth".
 *
 * @author FinanceManager Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user in the system.
     *
     * @param request The registration request containing user details
     * @return ApiResponse containing the result of the registration attempt
     * @throws BadRequestException if the request data is invalid
     * @throws ConflictException if the email is already registered
     */
    @PostMapping("/register")
    public ApiResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authenticates a user and creates a new session.
     *
     * @param request The login request containing credentials
     * @return ApiResponse containing the authentication result and session details
     * @throws UnauthorizedException if the credentials are invalid
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Invalidates the current user session.
     *
     * @return ApiResponse confirming the logout operation
     */
    @PostMapping("/logout")
    public ApiResponse logout() {
        return authService.logout();
    }
}

