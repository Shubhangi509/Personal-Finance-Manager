package com.pfm.FinanceManager.service.impl;


import com.pfm.FinanceManager.dto.*;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.UserRepository;
import com.pfm.FinanceManager.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    @Override
    public ApiResponse register(RegisterRequest request){
        log.info("Registration attempt for username: {}, phone: {}", request.getUsername(), request.getPhoneNumber());
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username '{}' already exists", request.getUsername());
            return new ApiResponse("Username already exists", null);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Registration failed - phone number '{}' already exists", request.getPhoneNumber());
            return new ApiResponse("Phone number already exists", null);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User saved = userRepository.save(user);
        log.info("User registered successfully with id: {} and username: {}", saved.getId(), saved.getUsername());
        return new ApiResponse("User registered successfully", saved.getId());
    }

    @Override
    public ApiResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        Object userId = session.getAttribute("user");
        if(userId != null) {
            log.warn("Logout current session to login again");
            return new ApiResponse("Logout current session to login again", null);
        }
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            log.error("Login failed - username not found: {}", request.getUsername());
            return new ApiResponse("Invalid credentials", null);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed - invalid password for username: {}", request.getUsername());
            return new ApiResponse("Invalid Credentials", null);
        }

        session.setAttribute("user", user.getId()); // Save user session
        log.info("Login successful for username: {}", request.getUsername());
        return new ApiResponse("Login successful", user.getId());
    }

    @Override
    public ApiResponse logout() {
        Object userId = session.getAttribute("user");
        if(userId != null) {
            session.invalidate();
            log.info("Logout successful for user id: {}", userId);
            return new ApiResponse("Logout successful", null);
        }
        log.warn("Logout attempted with no active session");
        return new ApiResponse("No active session", null);
    }
}

