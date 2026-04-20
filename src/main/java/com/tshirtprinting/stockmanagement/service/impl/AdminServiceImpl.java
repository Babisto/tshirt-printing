package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.entity.enums.UserRole;
import com.tshirtprinting.stockmanagement.exception.BusinessException;
import com.tshirtprinting.stockmanagement.exception.ResourceNotFoundException;
import com.tshirtprinting.stockmanagement.repository.AppUserRepository;
import com.tshirtprinting.stockmanagement.repository.PaintRepository;
import com.tshirtprinting.stockmanagement.repository.PaintUsageRepository;
import com.tshirtprinting.stockmanagement.repository.PrintJobRepository;
import com.tshirtprinting.stockmanagement.repository.ProductRepository;
import com.tshirtprinting.stockmanagement.repository.ProductVariantRepository;
import com.tshirtprinting.stockmanagement.repository.StockTransactionRepository;
import com.tshirtprinting.stockmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaintUsageRepository paintUsageRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final PrintJobRepository printJobRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final PaintRepository paintRepository;

    @Override
    @Transactional
    public void resetInventory(String email, String password) {
        var user = appUserRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        if (user.getRole() != UserRole.ROLE_ADMIN) {
            throw new BusinessException("Only admins can reset inventory");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("Admin password is incorrect");
        }

        paintUsageRepository.deleteAllInBatch();
        stockTransactionRepository.deleteAllInBatch();
        printJobRepository.deleteAllInBatch();
        productVariantRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        paintRepository.deleteAllInBatch();

        log.warn("Inventory reset triggered by admin {}", email);
    }
}
