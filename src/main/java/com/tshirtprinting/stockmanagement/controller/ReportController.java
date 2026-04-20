package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.report.PaintUsageReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.PrintJobReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.ReportResponse;
import com.tshirtprinting.stockmanagement.dto.report.StockReportItemResponse;
import com.tshirtprinting.stockmanagement.service.ReportService;
import java.security.Principal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/stock")
    public ResponseEntity<ReportResponse<StockReportItemResponse>> stockReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return ResponseEntity.ok(reportService.stockReport(from, to, principal.getName()));
    }

    @GetMapping("/paint-usage")
    public ResponseEntity<ReportResponse<PaintUsageReportItemResponse>> paintUsageReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return ResponseEntity.ok(reportService.paintUsageReport(from, to, principal.getName()));
    }

    @GetMapping("/print-jobs")
    public ResponseEntity<ReportResponse<PrintJobReportItemResponse>> printJobReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return ResponseEntity.ok(reportService.printJobReport(from, to, principal.getName()));
    }

    @GetMapping(value = "/stock/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportStockPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("stock-report.pdf", MediaType.APPLICATION_PDF, reportService.exportStockPdf(from, to, principal.getName()));
    }

    @GetMapping(value = "/stock/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportStockCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("stock-report.csv", MediaType.parseMediaType("text/csv"), reportService.exportStockCsv(from, to, principal.getName()));
    }

    @GetMapping(value = "/paint-usage/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPaintUsagePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("paint-usage-report.pdf", MediaType.APPLICATION_PDF, reportService.exportPaintUsagePdf(from, to, principal.getName()));
    }

    @GetMapping(value = "/paint-usage/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportPaintUsageCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("paint-usage-report.csv", MediaType.parseMediaType("text/csv"), reportService.exportPaintUsageCsv(from, to, principal.getName()));
    }

    @GetMapping(value = "/print-jobs/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPrintJobPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("print-jobs-report.pdf", MediaType.APPLICATION_PDF, reportService.exportPrintJobPdf(from, to, principal.getName()));
    }

    @GetMapping(value = "/print-jobs/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportPrintJobCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {
        return export("print-jobs-report.csv", MediaType.parseMediaType("text/csv"), reportService.exportPrintJobCsv(from, to, principal.getName()));
    }

    private ResponseEntity<byte[]> export(String fileName, MediaType mediaType, byte[] content) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(mediaType)
                .body(content);
    }
}
