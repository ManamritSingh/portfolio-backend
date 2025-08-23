package com.portfolio.backend.repository;

import com.portfolio.backend.model.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long> {

    // Since PersonalInfo is a singleton, we only need to get the first (and only) record
    @Query("SELECT p FROM PersonalInfo p ORDER BY p.id ASC LIMIT 1")
    Optional<PersonalInfo> findFirst();

    // Check if any personal info exists
    @Query("SELECT COUNT(p) > 0 FROM PersonalInfo p")
    boolean exists();

    // Find by email (useful for contact validation)
    Optional<PersonalInfo> findByEmail(String email);

    // Search by name (case insensitive)
    Optional<PersonalInfo> findByNameContainingIgnoreCase(String name);
}
