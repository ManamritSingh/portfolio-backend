package com.portfolio.backend.controller;

import com.portfolio.backend.model.PersonalInfo;
import com.portfolio.backend.repository.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
// Removed class-level @RequestMapping and @CrossOrigin for global config
public class PersonalInfoController {

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/personal-info")
    public ResponseEntity<PersonalInfo> getPersonalInfo() {
        // Public endpoint to get the single personal info record for the site header
        Optional<PersonalInfo> personalInfo = personalInfoRepository.findFirst();
        return personalInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // This endpoint could be useful for an admin panel check before editing
    @GetMapping("/api/public/personal-info/exists")
    public ResponseEntity<Boolean> personalInfoExists() {
        return ResponseEntity.ok(personalInfoRepository.exists());
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @PostMapping("/api/admin/personal-info")
    public ResponseEntity<PersonalInfo> createPersonalInfo(@RequestBody PersonalInfo personalInfo) {
        // Admin creates the personal info record. Only allow one.
        if (personalInfoRepository.count() > 0) {
            return ResponseEntity.badRequest().body(null); // Or some other error indicating it exists
        }
        PersonalInfo savedInfo = personalInfoRepository.save(personalInfo);
        return ResponseEntity.ok(savedInfo);
    }

    @PutMapping("/api/admin/personal-info")
    public ResponseEntity<PersonalInfo> updatePersonalInfo(@RequestBody PersonalInfo personalInfoDetails) {
        // Admin updates the existing personal info record. If it doesn't exist, it's created.
        Optional<PersonalInfo> optionalPersonalInfo = personalInfoRepository.findFirst();

        if (optionalPersonalInfo.isPresent()) {
            PersonalInfo personalInfo = optionalPersonalInfo.get();
            personalInfo.setName(personalInfoDetails.getName());
            personalInfo.setPhone(personalInfoDetails.getPhone());
            personalInfo.setLocation(personalInfoDetails.getLocation());
            personalInfo.setEmail(personalInfoDetails.getEmail());
            personalInfo.setLinkedinUrl(personalInfoDetails.getLinkedinUrl());

            return ResponseEntity.ok(personalInfoRepository.save(personalInfo));
        } else {
            // If for some reason no info exists, create it
            return ResponseEntity.ok(personalInfoRepository.save(personalInfoDetails));
        }
    }

    @DeleteMapping("/api/admin/personal-info/{id}")
    public ResponseEntity<?> deletePersonalInfo(@PathVariable Long id) {
        // Admin deletes the personal info record. Use with caution.
        if (personalInfoRepository.existsById(id)) {
            personalInfoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
