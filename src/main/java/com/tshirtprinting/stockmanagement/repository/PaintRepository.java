package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.Paint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaintRepository extends JpaRepository<Paint, Long> {

    Optional<Paint> findByIdAndDeletedFalse(Long id);

    Optional<Paint> findByIdAndOwnerEmailAndDeletedFalse(Long id, String ownerEmail);

    List<Paint> findByOwnerEmailAndDeletedFalse(String ownerEmail);
}
