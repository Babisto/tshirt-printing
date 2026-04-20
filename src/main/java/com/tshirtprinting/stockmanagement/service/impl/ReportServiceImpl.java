package com.tshirtprinting.stockmanagement.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tshirtprinting.stockmanagement.dto.report.PaintUsageReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.PrintJobReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.ReportResponse;
import com.tshirtprinting.stockmanagement.dto.report.ReportSummaryResponse;
import com.tshirtprinting.stockmanagement.dto.report.StockReportItemResponse;
import com.tshirtprinting.stockmanagement.entity.PaintUsage;
import com.tshirtprinting.stockmanagement.entity.PrintJob;
import com.tshirtprinting.stockmanagement.entity.StockTransaction;
import com.tshirtprinting.stockmanagement.service.ReportService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tshirtprinting.stockmanagement.repository.PaintUsageRepository;
import com.tshirtprinting.stockmanagement.repository.PrintJobRepository;
import com.tshirtprinting.stockmanagement.repository.StockTransactionRepository;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StockTransactionRepository stockTransactionRepository;
    private final PaintUsageRepository paintUsageRepository;
    private final PrintJobRepository printJobRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportResponse<StockReportItemResponse> stockReport(LocalDate from, LocalDate to, String ownerEmail) {
        LocalDateTime fromDate = atStartOfDay(from);
        LocalDateTime toDate = atEndOfDay(to);
        List<StockReportItemResponse> items = stockTransactionRepository.findHistory(
                        ownerEmail,
                        null,
                        fromDate,
                        toDate,
                        PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(this::toStockItem)
                .toList();
        BigDecimal totalRevenue = items.stream()
                .map(StockReportItemResponse::stockValueAfter)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = items.stream()
                .map(item -> item.retailPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResponse<>(
                "Stock Movement Report",
                from.toString(),
                to.toString(),
                summary(totalRevenue, totalCost),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse<PaintUsageReportItemResponse> paintUsageReport(LocalDate from, LocalDate to, String ownerEmail) {
        List<PaintUsageReportItemResponse> items = paintUsageRepository.findForReport(ownerEmail, atStartOfDay(from), atEndOfDay(to))
                .stream()
                .map(this::toPaintUsageItem)
                .toList();
        BigDecimal totalCost = items.stream().map(PaintUsageReportItemResponse::totalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResponse<>(
                "Paint Usage Report",
                from.toString(),
                to.toString(),
                summary(BigDecimal.ZERO, totalCost),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse<PrintJobReportItemResponse> printJobReport(LocalDate from, LocalDate to, String ownerEmail) {
        List<PrintJobReportItemResponse> items = printJobRepository.findForReport(ownerEmail, atStartOfDay(from), atEndOfDay(to))
                .stream()
                .map(this::toPrintJobItem)
                .toList();
        BigDecimal totalRevenue = items.stream().map(PrintJobReportItemResponse::retailValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = items.stream().map(PrintJobReportItemResponse::productionCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResponse<>(
                "Print Job Report",
                from.toString(),
                to.toString(),
                summary(totalRevenue, totalCost),
                items
        );
    }

    @Override
    public byte[] exportStockPdf(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<StockReportItemResponse> report = stockReport(from, to, ownerEmail);
        return buildPdf(report.reportName(), report.from(), report.to(), List.of("Date", "Product", "Variant", "Type", "Qty", "Balance", "Value"), report.items().stream()
                .map(item -> List.of(
                        item.date().toString(),
                        item.productName(),
                        item.variant(),
                        item.transactionType(),
                        String.valueOf(item.quantity()),
                        String.valueOf(item.balanceAfter()),
                        item.stockValueAfter().toPlainString()
                ))
                .toList());
    }

    @Override
    public byte[] exportStockCsv(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<StockReportItemResponse> report = stockReport(from, to, ownerEmail);
        return buildCsv(List.of("Date", "Product", "SKU", "Variant", "Type", "Quantity", "Balance", "Value", "Reference", "PerformedBy"),
                report.items().stream().map(item -> List.of(
                        item.date().toString(),
                        item.productName(),
                        item.sku(),
                        item.variant(),
                        item.transactionType(),
                        String.valueOf(item.quantity()),
                        String.valueOf(item.balanceAfter()),
                        item.stockValueAfter().toPlainString(),
                        item.reference(),
                        item.performedBy()
                )).toList());
    }

    @Override
    public byte[] exportPaintUsagePdf(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<PaintUsageReportItemResponse> report = paintUsageReport(from, to, ownerEmail);
        return buildPdf(report.reportName(), report.from(), report.to(), List.of("Date", "PrintJob", "Paint", "Type", "Color", "Qty Used", "Unit Cost", "Total Cost"), report.items().stream()
                .map(item -> List.of(
                        item.date().toString(),
                        item.printJobId().toString(),
                        item.paintName(),
                        item.paintType(),
                        item.color(),
                        item.quantityUsed().toPlainString(),
                        item.unitCost().toPlainString(),
                        item.totalCost().toPlainString()
                )).toList());
    }

    @Override
    public byte[] exportPaintUsageCsv(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<PaintUsageReportItemResponse> report = paintUsageReport(from, to, ownerEmail);
        return buildCsv(List.of("Date", "PrintJobId", "Paint", "PaintType", "Color", "QuantityUsed", "UnitCost", "TotalCost"),
                report.items().stream().map(item -> List.of(
                        item.date().toString(),
                        item.printJobId().toString(),
                        item.paintName(),
                        item.paintType(),
                        item.color(),
                        item.quantityUsed().toPlainString(),
                        item.unitCost().toPlainString(),
                        item.totalCost().toPlainString()
                )).toList());
    }

    @Override
    public byte[] exportPrintJobPdf(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<PrintJobReportItemResponse> report = printJobReport(from, to, ownerEmail);
        return buildPdf(report.reportName(), report.from(), report.to(), List.of("Date", "Product", "Variant", "Qty", "Status", "Cost", "Retail", "Profit"), report.items().stream()
                .map(item -> List.of(
                        item.date().toString(),
                        item.productName(),
                        item.variant(),
                        String.valueOf(item.quantityPrinted()),
                        item.status(),
                        item.productionCost().toPlainString(),
                        item.retailValue().toPlainString(),
                        item.estimatedProfit().toPlainString()
                )).toList());
    }

    @Override
    public byte[] exportPrintJobCsv(LocalDate from, LocalDate to, String ownerEmail) {
        ReportResponse<PrintJobReportItemResponse> report = printJobReport(from, to, ownerEmail);
        return buildCsv(List.of("PrintJobId", "Date", "Product", "Variant", "Qty", "Status", "ProductionCost", "RetailValue", "EstimatedProfit"),
                report.items().stream().map(item -> List.of(
                        item.printJobId().toString(),
                        item.date().toString(),
                        item.productName(),
                        item.variant(),
                        String.valueOf(item.quantityPrinted()),
                        item.status(),
                        item.productionCost().toPlainString(),
                        item.retailValue().toPlainString(),
                        item.estimatedProfit().toPlainString()
                )).toList());
    }

    private StockReportItemResponse toStockItem(StockTransaction transaction) {
        String variantLabel = transaction.getVariant().getSize() + " / " + transaction.getVariant().getColor();
        BigDecimal stockValueAfter = transaction.getVariant().getRetailPrice().multiply(BigDecimal.valueOf(transaction.getBalanceAfter()));
        return new StockReportItemResponse(
                transaction.getCreatedAt(),
                transaction.getVariant().getProduct().getName(),
                transaction.getVariant().getProduct().getSku(),
                variantLabel,
                transaction.getType().name(),
                transaction.getQuantity(),
                transaction.getBalanceAfter(),
                transaction.getVariant().getRetailPrice(),
                stockValueAfter,
                transaction.getReference(),
                transaction.getPerformedBy()
        );
    }

    private PaintUsageReportItemResponse toPaintUsageItem(PaintUsage usage) {
        BigDecimal totalCost = usage.getQuantityUsed().multiply(usage.getUnitCostAtUsage());
        return new PaintUsageReportItemResponse(
                usage.getCreatedAt(),
                usage.getPrintJob().getId(),
                usage.getPaint().getName(),
                usage.getPaint().getPaintType(),
                usage.getPaint().getColor(),
                usage.getQuantityUsed(),
                usage.getUnitCostAtUsage(),
                totalCost
        );
    }

    private PrintJobReportItemResponse toPrintJobItem(PrintJob printJob) {
        String variantLabel = printJob.getVariant() == null
                ? "N/A"
                : printJob.getVariant().getSize() + " / " + printJob.getVariant().getColor();
        BigDecimal profit = printJob.getRetailValue().subtract(printJob.getProductionCost());
        return new PrintJobReportItemResponse(
                printJob.getId(),
                printJob.getCreatedAt(),
                printJob.getProduct().getName(),
                variantLabel,
                printJob.getQuantityPrinted(),
                printJob.getStatus().name(),
                printJob.getProductionCost(),
                printJob.getRetailValue(),
                profit
        );
    }

    private ReportSummaryResponse summary(BigDecimal revenue, BigDecimal cost) {
        BigDecimal profit = revenue.subtract(cost);
        BigDecimal margin = BigDecimal.ZERO;
        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = profit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP);
        }
        return new ReportSummaryResponse(revenue, cost, margin);
    }

    private LocalDateTime atStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime atEndOfDay(LocalDate date) {
        return date.plusDays(1).atStartOfDay().minusNanos(1);
    }

    private byte[] buildPdf(String title, String from, String to, List<String> headers, List<List<String>> rows) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph(title));
            document.add(new Paragraph("Period: " + from + " to " + to));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(headers.size());
            headers.forEach(header -> table.addCell(headerCell(header)));
            rows.forEach(row -> row.forEach(value -> table.addCell(value)));
            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException ex) {
            throw new IllegalStateException("Failed to generate PDF", ex);
        }
    }

    private PdfPCell headerCell(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setPadding(5);
        return cell;
    }

    private byte[] buildCsv(List<String> headers, List<List<String>> rows) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers.toArray(String[]::new)).build())) {
            for (List<String> row : rows) {
                printer.printRecord(row);
            }
            printer.flush();
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to generate CSV", ex);
        }
    }
}
