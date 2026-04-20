package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.dto.paint.PaintRequest;
import com.tshirtprinting.stockmanagement.dto.paint.PaintResponse;
import com.tshirtprinting.stockmanagement.entity.Paint;
import com.tshirtprinting.stockmanagement.exception.ResourceNotFoundException;
import com.tshirtprinting.stockmanagement.mapper.PaintMapper;
import com.tshirtprinting.stockmanagement.repository.PaintRepository;
import com.tshirtprinting.stockmanagement.service.PaintService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaintServiceImpl implements PaintService {

    private final PaintRepository paintRepository;

    @Override
    @Transactional
    public PaintResponse create(PaintRequest request, String ownerEmail) {
        Paint paint = new Paint();
        applyRequest(paint, request, ownerEmail);
        Paint saved = paintRepository.save(paint);
        log.info("Created paint {} {}", saved.getPaintType(), saved.getColor());
        return PaintMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaintResponse update(Long id, PaintRequest request, String ownerEmail) {
        Paint paint = findPaint(id, ownerEmail);
        applyRequest(paint, request, ownerEmail);
        Paint saved = paintRepository.save(paint);
        log.info("Updated paint {}", id);
        return PaintMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaintResponse getById(Long id, String ownerEmail) {
        return PaintMapper.toResponse(findPaint(id, ownerEmail));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaintResponse> getAll(String ownerEmail) {
        return paintRepository.findByOwnerEmailAndDeletedFalse(ownerEmail).stream()
                .map(PaintMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id, String ownerEmail) {
        Paint paint = findPaint(id, ownerEmail);
        paint.setDeleted(Boolean.TRUE);
        paint.setActive(Boolean.FALSE);
        paintRepository.save(paint);
        log.info("Soft deleted paint {}", id);
    }

    private void applyRequest(Paint paint, PaintRequest request, String ownerEmail) {
        paint.setName(request.name());
        paint.setOwnerEmail(ownerEmail);
        paint.setPaintType(request.paintType());
        paint.setColor(request.color());
        paint.setQuantityAvailable(request.quantityAvailable());
        paint.setUnit(request.unit());
        paint.setCostPerUnit(request.costPerUnit());
        paint.setLowStockThreshold(request.lowStockThreshold());
        paint.setActive(request.active() == null ? Boolean.TRUE : request.active());
    }

    private Paint findPaint(Long id, String ownerEmail) {
        return paintRepository.findByIdAndOwnerEmailAndDeletedFalse(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Paint not found"));
    }
}
