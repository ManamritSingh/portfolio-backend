package com.portfolio.backend.repository;

import com.portfolio.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find visible projects ordered by orderIndex
    List<Project> findByIsVisibleTrueOrderByOrderIndexAsc();

    // Find all projects ordered by orderIndex
    List<Project> findAllByOrderByOrderIndexAsc();

    // Custom query to get max order index for auto-ordering
    @Query("SELECT COALESCE(MAX(p.orderIndex), 0) FROM Project p")
    Integer getMaxOrderIndex();
}
