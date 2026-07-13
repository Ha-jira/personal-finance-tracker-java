package com.financetracker.controller;

import com.financetracker.dto.BudgetRequest;
import com.financetracker.dto.BudgetResponse;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BudgetResponse> create(Authentication auth, @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.create(currentUser(auth), request));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll(Authentication auth,
                                                         @RequestParam(required = false) Integer month,
                                                         @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return ResponseEntity.ok(budgetService.getAll(currentUser(auth), m, y));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(Authentication auth, @PathVariable Long id,
                                                  @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.update(currentUser(auth), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        budgetService.delete(currentUser(auth), id);
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
