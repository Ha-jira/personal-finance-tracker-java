package com.financetracker.dto;

import com.financetracker.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Type is required (INCOME or EXPENSE)")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
