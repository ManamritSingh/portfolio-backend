package com.portfolio.backend.controller;

import com.portfolio.backend.model.Education;
import com.portfolio.backend.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Class-level annotations removed for specific path mapping and global CORS handling
public class EducationController {

    @Autowired
    private EducationRepository educationRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/education")
    public List<Education> getVisibleEducation() {
        // Main public endpoint to get all visible education entries
        return educationRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/education/graduate")
    public List<Education> getGraduateDegrees() {
        // Public endpoint to filter for graduate degrees
        return educationRepository.findGraduateDegrees();
    }

    @GetMapping("/api/public/education/undergraduate")
    public List<Education> getUndergraduateDegrees() {
        // Public endpoint to filter for undergraduate degrees
        return educationRepository.findUndergraduateDegrees();
    }

    @GetMapping("/api/public/education/institution/{institutionName}")
    public List<Education> getEducationByInstitution(@PathVariable String institutionName) {
        // Public endpoint to search for education by institution name
        return educationRepository.findByInstitutionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(institutionName);
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/education")
    public List<Education> getAllEducation() {
        // Admin gets all education entries, including hidden ones
        return educationRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/education/{id}")
    public ResponseEntity<Education> getEducation(@PathVariable Long id) {
        // Admin gets a single education entry by its ID
        Optional<Education> education = educationRepository.findById(id);
        return education.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/education")
    public Education createEducation(@RequestBody Education education) {
        // Admin creates a new education entry
        if (education.getOrderIndex() == null) {
            education.setOrderIndex(educationRepository.getMaxOrderIndex() + 1);
        }
        return educationRepository.save(education);
    }

    @PutMapping("/api/admin/education/{id}")
    public ResponseEntity<Education> updateEducation(@PathVariable Long id,
                                                     @RequestBody Education educationDetails) {
        // Admin updates an existing education entry
        Optional<Education> optionalEducation = educationRepository.findById(id);

        if (optionalEducation.isPresent()) {
            Education education = optionalEducation.get();
            education.setDegree(educationDetails.getDegree());
            education.setInstitution(educationDetails.getInstitution());
            education.setDuration(educationDetails.getDuration());
            education.setLocation(educationDetails.getLocation());
            education.setOrderIndex(educationDetails.getOrderIndex());
            education.setVisible(educationDetails.getVisible());

            return ResponseEntity.ok(educationRepository.save(education));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/education/{id}")
    public ResponseEntity<?> deleteEducation(@PathVariable Long id) {
        // Admin deletes an education entry
        if (educationRepository.existsById(id)) {
            educationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
