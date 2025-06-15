package com.pfm.FinanceManager.controller;

import com.pfm.FinanceManager.dto.CreateGoalRequest;
import com.pfm.FinanceManager.dto.GoalResponse;
import com.pfm.FinanceManager.dto.UpdateGoalRequest;
import com.pfm.FinanceManager.entity.SavingsGoal;
import com.pfm.FinanceManager.entity.User;
import com.pfm.FinanceManager.repository.UserRepository;
import com.pfm.FinanceManager.service.SavingsGoalService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller handling savings goals operations including creation, retrieval,
 * updating, and deletion of savings goals. All endpoints are prefixed with "/api/goals".
 * Supports tracking progress towards financial goals.
 *
 * @author FinanceManager Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final SavingsGoalService goalService;
    private final UserRepository userRepo;
    private final HttpSession session;

    /**
     * Creates a new savings goal.
     *
     * @param request The goal details including name, target amount, and dates
     * @return ResponseEntity containing the created goal and HTTP status
     */
    @PostMapping
    public ResponseEntity<SavingsGoal> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        SavingsGoal goal = goalService.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }

    /**
     * Retrieves all savings goals for the current user.
     *
     * @return List of GoalResponse objects containing goal details and progress
     * @throws UnauthorizedException if no active session exists
     */
    @GetMapping
    public List<GoalResponse> getAllGoals() {
        return goalService.getAllGoals();
    }

    /**
     * Retrieves a specific savings goal by ID.
     *
     * @param id The ID of the goal to retrieve
     * @return GoalResponse containing the goal details and progress
     * @throws ResourceNotFoundException if the goal doesn't exist
     * @throws ForbiddenException if the user doesn't own the goal
     */
    @GetMapping("/{id}")
    public GoalResponse getGoal(@PathVariable Long id) {
        return goalService.getGoalById(id);
    }

    /**
     * Updates an existing savings goal.
     *
     * @param id The ID of the goal to update
     * @param request The updated goal details
     * @return SavingsGoal containing the updated goal details
     * @throws ResourceNotFoundException if the goal doesn't exist
     * @throws ForbiddenException if the user doesn't own the goal
     * @throws BadRequestException if the request data is invalid
     */
    @PutMapping("/{id}")
    public SavingsGoal updateGoal(@PathVariable Long id, @RequestBody UpdateGoalRequest request) {
        return goalService.updateGoal(id, request);
    }

    /**
     * Deletes a savings goal.
     *
     * @param id The ID of the goal to delete
     * @return ResponseEntity containing a success message
     * @throws ResourceNotFoundException if the goal doesn't exist
     * @throws ForbiddenException if the user doesn't own the goal
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        String message = goalService.deleteGoal(id);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
