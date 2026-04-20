package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobRequest;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobResponse;
import com.tshirtprinting.stockmanagement.service.PrintJobService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/print-jobs")
@RequiredArgsConstructor
public class PrintJobController {

    private final PrintJobService printJobService;

    @PostMapping
    public ResponseEntity<PrintJobResponse> create(@Valid @RequestBody PrintJobRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(printJobService.create(request, principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintJobResponse> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(printJobService.getById(id, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PrintJobResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size,
                                                                 Principal principal) {
        return ResponseEntity.ok(printJobService.getAll(page, size, principal.getName()));
    }
}
