package com.portfolio.backend.repository;

import com.portfolio.backend.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    long countByIsVisibleTrue();

    // Find visible experiences ordered by orderIndex
    List<Experience> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all experiences ordered by orderIndex (most recent first usually)
    List<Experience> findAllByOrderByOrderIndexDesc();

    // Find current experiences (status = "Current")
    List<Experience> findByStatusAndIsVisibleTrue(String status);

    // Find experiences by company
    List<Experience> findByCompanyContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String company);

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(e.orderIndex), 0) FROM Experience e")
    Integer getMaxOrderIndex();

    // Find all current positions
    @Query("SELECT e FROM Experience e WHERE e.status = 'Current' AND e.isVisible = true ORDER BY e.orderIndex ASC")
    List<Experience> findCurrentPositions();
}
