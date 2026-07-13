package com.financetracker.service;

import com.financetracker.dto.BudgetRequest;
import com.financetracker.dto.BudgetResponse;
import com.financetracker.entity.Budget;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.entity.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetResponse create(User user, BudgetRequest request) {
        Budget budget = budgetRepository
                .findByUserAndCategoryAndMonthAndYear(user, request.getCategory(), request.getMonth(), request.getYear())
                .orElseGet(() -> Budget.builder()
                        .user(user)
                        .category(request.getCategory())
                        .month(request.getMonth())
                        .year(request.getYear())
                        .build());

        budget.setMonthlyLimit(request.getMonthlyLimit());
        budgetRepository.save(budget);

        return toResponse(user, budget);
    }

    public List<BudgetResponse> getAll(User user, int month, int year) {
        return budgetRepository.findByUserAndMonthAndYear(user, month, year)
                .stream()
                .map(b -> toResponse(user, b))
                .toList();
    }

    public BudgetResponse update(User user, Long id, BudgetRequest request) {
        Budget budget = getOwnedBudget(user, id);
        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budgetRepository.save(budget);
        return toResponse(user, budget);
    }

    public void delete(User user, Long id) {
        Budget budget = getOwnedBudget(user, id);
        budgetRepository.delete(budget);
    }

    private Budget getOwnedBudget(User user, Long id) {
        return budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalStateException("Budget not found"));
    }

    private BudgetResponse toResponse(User user, Budget budget) {
        LocalDate start = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal spent = transactionRepository
                .findByUserAndCategoryAndDateBetween(user, budget.getCategory(), start, end)
                .stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String status;
        BigDecimal ratio = spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.ONE) > 0) {
            status = "OVER_BUDGET";
        } else if (ratio.compareTo(new BigDecimal("0.8")) >= 0) {
            status = "WARNING";
        } else {
            status = "SAFE";
        }

        return new BudgetResponse(budget.getId(), budget.getCategory(), budget.getMonthlyLimit(), spent, budget.getMonth(), budget.getYear(), status);
    }
}
