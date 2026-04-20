package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.auth.AdminResetRequest;
import com.tshirtprinting.stockmanagement.service.AdminService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetInventory(@Valid @RequestBody AdminResetRequest request, Principal principal) {
        adminService.resetInventory(principal.getName(), request.password());
    }
}
