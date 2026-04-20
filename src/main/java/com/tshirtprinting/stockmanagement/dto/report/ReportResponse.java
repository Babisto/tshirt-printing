package com.tshirtprinting.stockmanagement.dto.report;

import java.util.List;

public record ReportResponse<T>(
        String reportName,
        String from,
        String to,
        ReportSummaryResponse summary,
        List<T> items
) {
}
