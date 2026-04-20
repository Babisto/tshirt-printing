package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.report.PaintUsageReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.PrintJobReportItemResponse;
import com.tshirtprinting.stockmanagement.dto.report.ReportResponse;
import com.tshirtprinting.stockmanagement.dto.report.StockReportItemResponse;
import java.time.LocalDate;

public interface ReportService {

    ReportResponse<StockReportItemResponse> stockReport(LocalDate from, LocalDate to, String ownerEmail);

    ReportResponse<PaintUsageReportItemResponse> paintUsageReport(LocalDate from, LocalDate to, String ownerEmail);

    ReportResponse<PrintJobReportItemResponse> printJobReport(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportStockPdf(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportStockCsv(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportPaintUsagePdf(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportPaintUsageCsv(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportPrintJobPdf(LocalDate from, LocalDate to, String ownerEmail);

    byte[] exportPrintJobCsv(LocalDate from, LocalDate to, String ownerEmail);
}
