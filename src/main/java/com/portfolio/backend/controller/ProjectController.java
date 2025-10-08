package com.portfolio.backend.controller;

import com.portfolio.backend.model.Project;
import com.portfolio.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Removed class-level @RequestMapping and @CrossOrigin for global CORS handling
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/projects")
    public List<Project> getVisibleProjects() {
        // Get all visible projects for display on public portfolio
        return projectRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/projects")
    public List<Project> getAllProjects() {
        // Admin can get all projects regardless of visibility
        return projectRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/projects/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        // Admin can get any single project by its ID
        Optional<Project> project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/projects")
    public Project createProject(@RequestBody Project project) {
        // Admin creates a new project
        if (project.getOrderIndex() == null) {
            project.setOrderIndex(projectRepository.getMaxOrderIndex() + 1);
        }
        return projectRepository.save(project);
    }

    @PutMapping("/api/admin/projects/reorder")
    public ResponseEntity<List<Project>> reorderProjects(@RequestBody List<Project> projects) {
        // Admin-only endpoint for batch-updating project order
        try {
            List<Project> updatedProjects = projectRepository.saveAll(projects);
            return ResponseEntity.ok(updatedProjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/admin/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        // Admin updates an existing project
        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            project.setTitle(projectDetails.getTitle());
            project.setSubtitle(projectDetails.getSubtitle());
            project.setBulletPoints(projectDetails.getBulletPoints());
            project.setUrl(projectDetails.getUrl());
            project.setOrderIndex(projectDetails.getOrderIndex());
            project.setVisible(projectDetails.getVisible());

            return ResponseEntity.ok(projectRepository.save(project));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/projects/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        // Admin deletes a project
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
