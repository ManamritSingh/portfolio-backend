package com.portfolio.backend.repository;

import com.portfolio.backend.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Find visible sections ordered by orderIndex
    List<Section> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all sections ordered by orderIndex
    List<Section> findAllByOrderByOrderIndexAsc();

    // Find section by name (case insensitive)
    Optional<Section> findBySectionNameIgnoreCase(String sectionName);

    // Check if section with name exists
    boolean existsBySectionNameIgnoreCase(String sectionName);

    // Search sections by name containing keyword
    List<Section> findBySectionNameContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String keyword);

    // Find sections created after a certain date
    @Query("SELECT s FROM Section s WHERE s.createdAt >= ?1 ORDER BY s.createdAt DESC")
    List<Section> findRecentSections(java.time.LocalDateTime since);

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Section s")
    Integer getMaxOrderIndex();

    // Find sections with descriptions
    List<Section> findByDescriptionIsNotNullAndIsVisibleTrueOrderByOrderIndexAsc();
}
