package com.pfm.FinanceManager.service.impl;

import com.pfm.FinanceManager.dto.ApiResponse;
import com.pfm.FinanceManager.dto.LoginRequest;
import com.pfm.FinanceManager.dto.RegisterRequest;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFullName("Test User");
        testUser.setPhoneNumber("+1234567890");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ApiResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertEquals(testUser.getId(), response.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        ApiResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Username already exists", response.getMessage());
        assertNull(response.getUserId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_DuplicatePhoneNumber() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())).thenReturn(true);

        ApiResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Phone number already exists", response.getMessage());
        assertNull(response.getUserId());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(session.getAttribute("user")).thenReturn(null);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);

        ApiResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(testUser.getId(), response.getUserId());
        verify(session).setAttribute("user", testUser.getId());
    }

    @Test
    void login_UserNotFound() {
        when(session.getAttribute("user")).thenReturn(null);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        ApiResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Invalid credentials", response.getMessage());
        assertNull(response.getUserId());
        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void login_InvalidPassword() {
        when(session.getAttribute("user")).thenReturn(null);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        ApiResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Invalid Credentials", response.getMessage());
        assertNull(response.getUserId());
        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void login_ActiveSession() {
        when(session.getAttribute("user")).thenReturn(1L);

        ApiResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Logout current session to login again", response.getMessage());
        assertNull(response.getUserId());
        verify(session, never()).setAttribute(any(), any());
    }

    @Test
    void logout_Success() {
        when(session.getAttribute("user")).thenReturn(1L);

        ApiResponse response = authService.logout();

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        assertNull(response.getUserId());
        verify(session).invalidate();
    }

    @Test
    void logout_NoActiveSession() {
        when(session.getAttribute("user")).thenReturn(null);

        ApiResponse response = authService.logout();

        assertNotNull(response);
        assertEquals("No active session", response.getMessage());
        assertNull(response.getUserId());
        verify(session, never()).invalidate();
    }
} 