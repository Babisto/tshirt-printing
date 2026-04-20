package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.paint.PaintRequest;
import com.tshirtprinting.stockmanagement.dto.paint.PaintResponse;
import com.tshirtprinting.stockmanagement.service.PaintService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paints")
@RequiredArgsConstructor
public class PaintController {

    private final PaintService paintService;

    @PostMapping
    public ResponseEntity<PaintResponse> create(@Valid @RequestBody PaintRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paintService.create(request, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaintResponse> update(@PathVariable Long id, @Valid @RequestBody PaintRequest request, Principal principal) {
        return ResponseEntity.ok(paintService.update(id, request, principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaintResponse> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(paintService.getById(id, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PaintResponse>> getAll(Principal principal) {
        return ResponseEntity.ok(paintService.getAll(principal.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Principal principal) {
        paintService.delete(id, principal.getName());
    }
}
