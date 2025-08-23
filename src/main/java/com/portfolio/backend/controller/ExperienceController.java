package com.portfolio.backend.controller;

import com.portfolio.backend.model.Experience;
import com.portfolio.backend.repository.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/experience")
@CrossOrigin(origins = "http://localhost:5173")
public class ExperienceController {

    @Autowired
    private ExperienceRepository experienceRepository;

    // Get all visible experiences (for public resume) - most recent first
    @GetMapping("/public")
    public List<Experience> getVisibleExperiences() {
        return experienceRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get current positions only
    @GetMapping("/current")
    public List<Experience> getCurrentPositions() {
        return experienceRepository.findCurrentPositions();
    }

    // Get experiences by company
    @GetMapping("/company/{companyName}")
    public List<Experience> getExperiencesByCompany(@PathVariable String companyName) {
        return experienceRepository.findByCompanyContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(companyName);
    }

    // Get all experiences (for admin) - most recent first
    @GetMapping
    public List<Experience> getAllExperiences() {
        return experienceRepository.findAllByOrderByOrderIndexDesc();
    }

    // Get single experience
    @GetMapping("/{id}")
    public ResponseEntity<Experience> getExperience(@PathVariable Long id) {
        Optional<Experience> experience = experienceRepository.findById(id);
        return experience.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new experience
    @PostMapping
    public Experience createExperience(@RequestBody Experience experience) {
        // Auto-set order index if not provided
        if (experience.getOrderIndex() == null) {
            experience.setOrderIndex(experienceRepository.getMaxOrderIndex() + 1);
        }
        return experienceRepository.save(experience);
    }

    // Update experience
    @PutMapping("/{id}")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long id,
                                                       @RequestBody Experience experienceDetails) {
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

    // Delete experience
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperience(@PathVariable Long id) {
        if (experienceRepository.existsById(id)) {
            experienceRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
