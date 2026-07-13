package com.financetracker.service;

import com.financetracker.dto.TransactionRequest;
import com.financetracker.dto.TransactionResponse;
import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponse create(User user, TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(request.getType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    public List<TransactionResponse> getAll(User user, Integer month, Integer year) {
        List<Transaction> transactions;

        if (month != null && year != null) {
            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
            transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDesc(user);
        }

        return transactions.stream().map(this::toResponse).toList();
    }

    public TransactionResponse update(User user, Long id, TransactionRequest request) {
        Transaction transaction = getOwnedTransaction(user, id);

        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());

        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    public void delete(User user, Long id) {
        Transaction transaction = getOwnedTransaction(user, id);
        transactionRepository.delete(transaction);
    }

    private Transaction getOwnedTransaction(User user, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You do not have access to this transaction");
        }
        return transaction;
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(t.getId(), t.getType(), t.getCategory(), t.getAmount(), t.getDescription(), t.getDate());
    }
}
