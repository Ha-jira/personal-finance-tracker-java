package com.financetracker.dto;

import com.financetracker.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
}
