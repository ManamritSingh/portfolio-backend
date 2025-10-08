package com.portfolio.backend.controller;

import com.portfolio.backend.model.Experience;
import com.portfolio.backend.repository.ExperienceRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Remove the single @RequestMapping - we'll use specific paths instead
// Remove @CrossOrigin - now handled globally in SecurityConfig
public class ExperienceController {

    @Autowired
    private ExperienceRepository experienceRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // These serve your public portfolio/resume
    // ===========================================

    @GetMapping("/api/public/experience")
    public List<Experience> getVisibleExperiences() {
        // This replaces your old "/public" endpoint
        // Only returns experiences marked as visible, ordered properly
        return experienceRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/experience/current")
    public List<Experience> getCurrentPositions() {
        // Public endpoint to show current positions on your portfolio
        return experienceRepository.findCurrentPositions();
    }

    @GetMapping("/api/public/experience/company/{companyName}")
    public List<Experience> getExperiencesByCompany(@PathVariable String companyName) {
        // Public endpoint to filter experiences by company
        // Still only shows visible experiences
        return experienceRepository.findByCompanyContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(companyName);
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // These are protected by Spring Security
    // ===========================================

    @GetMapping("/api/admin/experience")
    public List<Experience> getAllExperiences() {
        // Admin can see ALL experiences (visible and hidden)
        // This was your old root GET endpoint
        return experienceRepository.findAllByOrderByOrderIndexDesc();
    }

    @GetMapping("/api/admin/experience/{id}")
    public ResponseEntity<Experience> getExperience(@PathVariable Long id) {
        // Admin can get any specific experience by ID
        Optional<Experience> experience = experienceRepository.findById(id);
        return experience.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/experience")
    public Experience createExperience(@RequestBody Experience experience) {
        // Only admins can create new experiences
        // Auto-set order index if not provided
        if (experience.getOrderIndex() == null) {
            experience.setOrderIndex(experienceRepository.getMaxOrderIndex() + 1);
        }
        return experienceRepository.save(experience);
    }

    @PutMapping("/api/admin/experience/{id}")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long id,
                                                       @RequestBody Experience experienceDetails) {
        // Only admins can update experiences
        Optional<Experience> optionalExperience = experienceRepository.findById(id);

        if (optionalExperience.isPresent()) {
            Experience experience = optionalExperience.get();
            experience.setPosition(experienceDetails.getPosition());
            experience.setDuration(experienceDetails.getDuration());
            experience.setCompany(experienceDetails.getCompany());
            experience.setLocation(experienceDetails.getLocation());
            experience.setStatus(experienceDetails.getStatus());
            experience.setBulletPoints(experienceDetails.getBulletPoints());
            experience.setOrderIndex(experienceDetails.getOrderIndex());
            experience.setVisible(experienceDetails.getVisible());

            return ResponseEntity.ok(experienceRepository.save(experience));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/experience/{id}")
    public ResponseEntity<?> deleteExperience(@PathVariable Long id) {
        // Only admins can delete experiences
        if (experienceRepository.existsById(id)) {
            experienceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/admin/dashboard")
    public ResponseEntity<String> dashboard(HttpServletRequest request) {
        // This will show exactly what path Spring sees
        System.out.println("Request URI: '" + request.getRequestURI() + "'");
        return ResponseEntity.ok("Admin Dashboard");
    }
}
