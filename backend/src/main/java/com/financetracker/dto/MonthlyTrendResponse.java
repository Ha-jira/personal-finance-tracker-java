package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyTrendResponse {
    private int month;
    private BigDecimal income;
    private BigDecimal expense;
}
