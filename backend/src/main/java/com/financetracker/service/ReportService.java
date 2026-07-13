package com.financetracker.service;

import com.financetracker.entity.Transaction;
import com.financetracker.entity.TransactionType;
import com.financetracker.entity.User;
import com.financetracker.repository.TransactionRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getTransactions(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
    }

    public byte[] generateCsv(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Type,Category,Amount,Description\n");
        for (Transaction t : transactions) {
            sb.append(t.getDate()).append(",")
              .append(t.getType()).append(",")
              .append(escapeCsv(t.getCategory())).append(",")
              .append(t.getAmount()).append(",")
              .append(escapeCsv(t.getDescription() == null ? "" : t.getDescription()))
              .append("\n");
        }
        return sb.toString().getBytes();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public byte[] generateExcel(List<Transaction> transactions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transactions");

            Row header = sheet.createRow(0);
            String[] columns = {"Date", "Type", "Category", "Amount", "Description"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIndex = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(t.getDate().toString());
                row.createCell(1).setCellValue(t.getType().toString());
                row.createCell(2).setCellValue(t.getCategory());
                row.createCell(3).setCellValue(t.getAmount().doubleValue());
                row.createCell(4).setCellValue(t.getDescription() == null ? "" : t.getDescription());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate Excel report", e);
        }
    }

    public byte[] generatePdf(String title, List<Transaction> transactions) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph(title, titleFont));
            document.add(new Paragraph(" "));

            BigDecimal totalIncome = sumByType(transactions, TransactionType.INCOME);
            BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);

            Font normalFont = new Font(Font.HELVETICA, 11);
            document.add(new Paragraph("Total Income: " + totalIncome, normalFont));
            document.add(new Paragraph("Total Expense: " + totalExpense, normalFont));
            document.add(new Paragraph("Net Savings: " + totalIncome.subtract(totalExpense), normalFont));
            document.add(new Paragraph(" "));

            Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
            for (Transaction t : transactions) {
                if (t.getType() == TransactionType.EXPENSE) {
                    categoryTotals.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
                }
            }

            if (!categoryTotals.isEmpty()) {
                document.add(new Paragraph("Category-wise Spending", new Font(Font.HELVETICA, 13, Font.BOLD)));
                PdfPTable catTable = new PdfPTable(2);
                catTable.setWidthPercentage(100);
                addHeaderCell(catTable, "Category");
                addHeaderCell(catTable, "Total Spent");
                for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
                    catTable.addCell(entry.getKey());
                    catTable.addCell(entry.getValue().toString());
                }
                document.add(catTable);
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("Transactions", new Font(Font.HELVETICA, 13, Font.BOLD)));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addHeaderCell(table, "Date");
            addHeaderCell(table, "Type");
            addHeaderCell(table, "Category");
            addHeaderCell(table, "Amount");
            addHeaderCell(table, "Description");

            for (Transaction t : transactions) {
                table.addCell(t.getDate().toString());
                table.addCell(t.getType().toString());
                table.addCell(t.getCategory());
                table.addCell(t.getAmount().toString());
                table.addCell(t.getDescription() == null ? "" : t.getDescription());
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate PDF report", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.HELVETICA, 11, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
