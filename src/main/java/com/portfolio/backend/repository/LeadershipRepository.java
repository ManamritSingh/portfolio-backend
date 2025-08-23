package com.portfolio.backend.repository;

import com.portfolio.backend.model.Leadership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadershipRepository extends JpaRepository<Leadership, Long> {

    // Find visible leadership ordered by orderIndex
    List<Leadership> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all leadership ordered by orderIndex
    List<Leadership> findAllByOrderByOrderIndexAsc();

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Leadership l")
    Integer getMaxOrderIndex();

    // Search leadership by keyword in description
    List<Leadership> findByDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String keyword);

    // Count total leadership entries
    long countByIsVisibleTrue();
}
