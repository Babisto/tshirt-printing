package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobRequest;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobResponse;

public interface PrintJobService {

    PrintJobResponse create(PrintJobRequest request, String actor);

    PrintJobResponse getById(Long id, String actor);

    PageResponse<PrintJobResponse> getAll(int page, int size, String actor);
}
