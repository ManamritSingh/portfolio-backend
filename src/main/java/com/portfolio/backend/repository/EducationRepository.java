package com.portfolio.backend.repository;

import com.portfolio.backend.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    // Find visible education entries ordered by orderIndex
    List<Education> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all education entries ordered by orderIndex (most recent first usually)
    List<Education> findAllByOrderByOrderIndexAsc();

    // Find education by institution
    List<Education> findByInstitutionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String institution);

    // Find education by degree type
    List<Education> findByDegreeContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String degree);

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(e.orderIndex), 0) FROM Education e")
    Integer getMaxOrderIndex();

    // Find graduate degrees (MS, PhD, etc.)
    @Query("SELECT e FROM Education e WHERE (LOWER(e.degree) LIKE 'ms%' OR LOWER(e.degree) LIKE 'master%' OR LOWER(e.degree) LIKE 'phd%') AND e.isVisible = true ORDER BY e.orderIndex ASC")
    List<Education> findGraduateDegrees();

    // Find undergraduate degrees
    @Query("SELECT e FROM Education e WHERE (LOWER(e.degree) LIKE 'b.%' OR LOWER(e.degree) LIKE 'bachelor%') AND e.isVisible = true ORDER BY e.orderIndex ASC")
    List<Education> findUndergraduateDegrees();
}
