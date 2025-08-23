package com.portfolio.backend.controller;

import com.portfolio.backend.model.Education;
import com.portfolio.backend.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/education")
@CrossOrigin(origins = "http://localhost:5173")
public class EducationController {

    @Autowired
    private EducationRepository educationRepository;

    // Get all visible education entries (for public resume)
    @GetMapping("/public")
    public List<Education> getVisibleEducation() {
        return educationRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get graduate degrees only
    @GetMapping("/graduate")
    public List<Education> getGraduateDegrees() {
        return educationRepository.findGraduateDegrees();
    }

    // Get undergraduate degrees only
    @GetMapping("/undergraduate")
    public List<Education> getUndergraduateDegrees() {
        return educationRepository.findUndergraduateDegrees();
    }

    // Get education by institution
    @GetMapping("/institution/{institutionName}")
    public List<Education> getEducationByInstitution(@PathVariable String institutionName) {
        return educationRepository.findByInstitutionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(institutionName);
    }

    // Get all education entries (for admin)
    @GetMapping
    public List<Education> getAllEducation() {
        return educationRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single education entry
    @GetMapping("/{id}")
    public ResponseEntity<Education> getEducation(@PathVariable Long id) {
        Optional<Education> education = educationRepository.findById(id);
        return education.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new education entry
    @PostMapping
    public Education createEducation(@RequestBody Education education) {
        // Auto-set order index if not provided
        if (education.getOrderIndex() == null) {
            education.setOrderIndex(educationRepository.getMaxOrderIndex() + 1);
        }
        return educationRepository.save(education);
    }

    // Update education entry
    @PutMapping("/{id}")
    public ResponseEntity<Education> updateEducation(@PathVariable Long id,
                                                     @RequestBody Education educationDetails) {
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

    // Delete education entry
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEducation(@PathVariable Long id) {
        if (educationRepository.existsById(id)) {
            educationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
