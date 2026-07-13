package com.financetracker.service;

import com.financetracker.dto.CategoryBreakdownResponse;
import com.financetracker.dto.DashboardSummaryResponse;
import com.financetracker.dto.MonthlyTrendResponse;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummaryResponse getSummary(User user, int month, int year) {
        List<Transaction> transactions = transactionsForMonth(user, month, year);

        BigDecimal income = sumByType(transactions, TransactionType.INCOME);
        BigDecimal expense = sumByType(transactions, TransactionType.EXPENSE);

        return new DashboardSummaryResponse(income, expense, income.subtract(expense));
    }

    public List<CategoryBreakdownResponse> getCategoryBreakdown(User user, int month, int year) {
        List<Transaction> transactions = transactionsForMonth(user, month, year);

        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                totals.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
            }
        }

        return totals.entrySet().stream()
                .map(e -> new CategoryBreakdownResponse(e.getKey(), e.getValue()))
                .toList();
    }

    public List<MonthlyTrendResponse> getMonthlyTrend(User user, int year) {
        List<MonthlyTrendResponse> trend = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            List<Transaction> transactions = transactionsForMonth(user, month, year);
            BigDecimal income = sumByType(transactions, TransactionType.INCOME);
            BigDecimal expense = sumByType(transactions, TransactionType.EXPENSE);
            trend.add(new MonthlyTrendResponse(month, income, expense));
        }

        return trend;
    }

    private List<Transaction> transactionsForMonth(User user, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
