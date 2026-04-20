package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.paint.PaintRequest;
import com.tshirtprinting.stockmanagement.dto.paint.PaintResponse;
import java.util.List;

public interface PaintService {

    PaintResponse create(PaintRequest request, String ownerEmail);

    PaintResponse update(Long id, PaintRequest request, String ownerEmail);

    PaintResponse getById(Long id, String ownerEmail);

    List<PaintResponse> getAll(String ownerEmail);

    void delete(Long id, String ownerEmail);
}
