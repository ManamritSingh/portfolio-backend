package com.portfolio.backend.controller;

import com.portfolio.backend.model.Project;
import com.portfolio.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    // Get all visible projects (for public resume)
    @GetMapping("/public")
    public List<Project> getVisibleProjects() {
        return projectRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all projects (for admin)
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single project
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new project
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        // Auto-set order index if not provided
        if (project.getOrderIndex() == null) {
            project.setOrderIndex(projectRepository.getMaxOrderIndex() + 1);
        }
        return projectRepository.save(project);
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<Project>> reorderProjects(@RequestBody List<Project> projects) {
        try {
            List<Project> updatedProjects = projectRepository.saveAll(projects);
            return ResponseEntity.ok(updatedProjects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update project
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
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

    // Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
