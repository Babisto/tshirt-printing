package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndDeletedFalse(String email);
}
