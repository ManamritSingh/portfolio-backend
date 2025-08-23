package com.portfolio.backend.repository;

import com.portfolio.backend.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    // Find visible certifications ordered by orderIndex
    List<Certification> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all certifications ordered by orderIndex
    List<Certification> findAllByOrderByOrderIndexAsc();

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(c.orderIndex), 0) FROM Certification c")
    Integer getMaxOrderIndex();

    // Search certifications by name or issuer
    List<Certification> findByNameContainingIgnoreCaseOrIssuerContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String name, String issuer);

    // Count total certifications
    long countByIsVisibleTrue();
}
