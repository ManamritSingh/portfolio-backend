package com.portfolio.backend.repository;

import com.portfolio.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    // Find visible skills ordered by orderIndex
    List<Skill> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all skills ordered by orderIndex
    List<Skill> findAllByOrderByOrderIndexAsc();

    // Find skills by category (exact match, case insensitive)
    List<Skill> findByCategoryIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String category);

    // Find skill by category (case insensitive) - useful for updating specific categories
    Optional<Skill> findByCategoryIgnoreCase(String category);

    // Check if category exists
    boolean existsByCategoryIgnoreCase(String category);

    // Search skills by keyword in skillsList
    List<Skill> findBySkillsListContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String keyword);

    // Find categories containing specific keyword
    List<Skill> findByCategoryContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(String keyword);

    long countByIsVisibleTrue();

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(s.orderIndex), 0) FROM Skill s")
    Integer getMaxOrderIndex();

    // Get all unique categories (for dropdown/filtering)
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.isVisible = true ORDER BY s.category")
    List<String> findAllUniqueCategories();

    // Find technical skills specifically (based on your resume categories)
    @Query("SELECT s FROM Skill s WHERE LOWER(s.category) LIKE '%technical%' AND s.isVisible = true ORDER BY s.orderIndex ASC")
    List<Skill> findTechnicalSkills();

    // Find other/soft skills
    @Query("SELECT s FROM Skill s WHERE LOWER(s.category) LIKE '%other%' AND s.isVisible = true ORDER BY s.orderIndex ASC")
    List<Skill> findOtherSkills();

    // Find hobbies and passions
    @Query("SELECT s FROM Skill s WHERE LOWER(s.category) LIKE '%hobbies%' OR LOWER(s.category) LIKE '%passion%' AND s.isVisible = true ORDER BY s.orderIndex ASC")
    List<Skill> findHobbiesAndPassions();
}
