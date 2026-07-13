package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private String category;
    private BigDecimal monthlyLimit;
    private BigDecimal spent;
    private int month;
    private int year;
    private String status;
}
