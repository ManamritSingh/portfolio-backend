package com.portfolio.backend.repository;

import com.portfolio.backend.model.SectionContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionContentRepository extends JpaRepository<SectionContent, Long> {

    // Find content by section ID, visible only, ordered by orderIndex
    List<SectionContent> findBySectionIdAndIsVisibleTrueOrderByOrderIndexAsc(Long sectionId);

    // Find all content by section ID ordered by orderIndex
    List<SectionContent> findBySectionIdOrderByOrderIndexAsc(Long sectionId);

    // Find visible content by section name
    @Query("SELECT sc FROM SectionContent sc JOIN sc.section s WHERE LOWER(s.sectionName) = LOWER(:sectionName) AND sc.isVisible = true ORDER BY sc.orderIndex ASC")
    List<SectionContent> findBySectionNameAndVisible(@Param("sectionName") String sectionName);

    // Find content with URLs (for linking)
    List<SectionContent> findBySectionIdAndPrimaryUrlIsNotNullAndIsVisibleTrueOrderByOrderIndexAsc(Long sectionId);

    // Find content by title containing keyword within a section
    List<SectionContent> findBySectionIdAndTitleContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(Long sectionId, String keyword);

    // Search content description by keyword within a section
    List<SectionContent> findBySectionIdAndDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(Long sectionId, String keyword);

    // Get max order index for a specific section (for auto-ordering)
    @Query("SELECT COALESCE(MAX(sc.orderIndex), 0) FROM SectionContent sc WHERE sc.section.id = :sectionId")
    Integer getMaxOrderIndexForSection(@Param("sectionId") Long sectionId);

    // Count content items in a section
    long countBySectionIdAndIsVisibleTrue(Long sectionId);

    // Find all visible content across all sections (for global search)
    List<SectionContent> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find content with bullet points
    @Query("SELECT sc FROM SectionContent sc WHERE sc.bulletPoints IS NOT EMPTY AND sc.isVisible = true AND sc.section.id = :sectionId ORDER BY sc.orderIndex ASC")
    List<SectionContent> findContentWithBulletPoints(@Param("sectionId") Long sectionId);
}
