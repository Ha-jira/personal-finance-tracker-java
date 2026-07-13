package com.financetracker.controller;

import com.financetracker.dto.TransactionRequest;
import com.financetracker.dto.TransactionResponse;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.TransactionService;
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

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(Authentication auth, @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.create(currentUser(auth), request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(Authentication auth,
                                                              @RequestParam(required = false) Integer month,
                                                              @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(transactionService.getAll(currentUser(auth), month, year));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(Authentication auth, @PathVariable Long id,
                                                        @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(currentUser(auth), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        transactionService.delete(currentUser(auth), id);
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
