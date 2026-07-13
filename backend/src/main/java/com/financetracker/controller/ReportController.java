package com.financetracker.controller;

import com.financetracker.entity.Transaction;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    @GetMapping("/monthly")
    public ResponseEntity<ByteArrayResource> monthly(Authentication auth,
                                                       @RequestParam int month,
                                                       @RequestParam int year,
                                                       @RequestParam(defaultValue = "csv") String format) {
        User user = currentUser(auth);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Transaction> transactions = reportService.getTransactions(user, start, end);

        String title = "Monthly Expense Report - " + start.getMonth() + " " + year;
        String filenamePrefix = "monthly-report-" + year + "-" + month;

        return buildFileResponse(transactions, format, title, filenamePrefix);
    }

    @GetMapping("/annual")
    public ResponseEntity<ByteArrayResource> annual(Authentication auth,
                                                      @RequestParam int year,
                                                      @RequestParam(defaultValue = "csv") String format) {
        User user = currentUser(auth);
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Transaction> transactions = reportService.getTransactions(user, start, end);

        String title = "Annual Financial Summary - " + year;
        String filenamePrefix = "annual-report-" + year;

        return buildFileResponse(transactions, format, title, filenamePrefix);
    }

    private ResponseEntity<ByteArrayResource> buildFileResponse(List<Transaction> transactions, String format,
                                                                  String title, String filenamePrefix) {
        byte[] content;
        String filename;
        MediaType mediaType;

        String fmt = format.toLowerCase();
        if (fmt.equals("pdf")) {
            content = reportService.generatePdf(title, transactions);
            filename = filenamePrefix + ".pdf";
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fmt.equals("excel") || fmt.equals("xlsx")) {
            content = reportService.generateExcel(transactions);
            filename = filenamePrefix + ".xlsx";
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            content = reportService.generateCsv(transactions);
            filename = filenamePrefix + ".csv";
            mediaType = MediaType.parseMediaType("text/csv");
        }

        ByteArrayResource resource = new ByteArrayResource(content);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .contentLength(content.length)
                .body(resource);
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
