package com.pfm.FinanceManager.util;

import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final HttpSession session;
    private final UserRepository userRepo;

    public User getSessionUser() {
        Long userId = (Long) session.getAttribute("user");
        if (userId == null) {
            log.warn("Attempted to access category without an active session");
            throw new RuntimeException("User not logged in");
        }
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("Session user not found in database with ID: {}", userId);
                    return new RuntimeException("User not found");
                });
    }
}
