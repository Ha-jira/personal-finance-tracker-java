package com.financetracker.controller;

import com.financetracker.dto.CategoryBreakdownResponse;
import com.financetracker.dto.DashboardSummaryResponse;
import com.financetracker.dto.MonthlyTrendResponse;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> summary(Authentication auth,
                                                              @RequestParam(required = false) Integer month,
                                                              @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        return ResponseEntity.ok(dashboardService.getSummary(currentUser(auth),
                month != null ? month : now.getMonthValue(),
                year != null ? year : now.getYear()));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownResponse>> categoryBreakdown(Authentication auth,
                                                                               @RequestParam(required = false) Integer month,
                                                                               @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        return ResponseEntity.ok(dashboardService.getCategoryBreakdown(currentUser(auth),
                month != null ? month : now.getMonthValue(),
                year != null ? year : now.getYear()));
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendResponse>> monthlyTrend(Authentication auth,
                                                                     @RequestParam(required = false) Integer year) {
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(dashboardService.getMonthlyTrend(currentUser(auth), y));
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
