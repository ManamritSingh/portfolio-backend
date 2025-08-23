package com.portfolio.backend.controller;

import com.portfolio.backend.model.PersonalInfo;
import com.portfolio.backend.repository.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/personal-info")
@CrossOrigin(origins = "http://localhost:5173")
public class PersonalInfoController {

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    // Get current personal info (for both public and admin use)
    @GetMapping
    public ResponseEntity<PersonalInfo> getPersonalInfo() {
        Optional<PersonalInfo> personalInfo = personalInfoRepository.findFirst();
        return personalInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get personal info by ID (fallback method)
    @GetMapping("/{id}")
    public ResponseEntity<PersonalInfo> getPersonalInfoById(@PathVariable Long id) {
        Optional<PersonalInfo> personalInfo = personalInfoRepository.findById(id);
        return personalInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create personal info (only if none exists)
    @PostMapping
    public ResponseEntity<PersonalInfo> createPersonalInfo(@RequestBody PersonalInfo personalInfo) {
        // Check if personal info already exists
        if (personalInfoRepository.exists()) {
            return ResponseEntity.badRequest().build(); // Don't allow multiple records
        }

        PersonalInfo savedInfo = personalInfoRepository.save(personalInfo);
        return ResponseEntity.ok(savedInfo);
    }

    // Update personal info (update the existing record)
    @PutMapping
    public ResponseEntity<PersonalInfo> updatePersonalInfo(@RequestBody PersonalInfo personalInfoDetails) {
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
            // If no personal info exists, create new one
            return ResponseEntity.ok(personalInfoRepository.save(personalInfoDetails));
        }
    }

    // Update personal info by ID (alternative method)
    @PutMapping("/{id}")
    public ResponseEntity<PersonalInfo> updatePersonalInfoById(@PathVariable Long id,
                                                               @RequestBody PersonalInfo personalInfoDetails) {
        Optional<PersonalInfo> optionalPersonalInfo = personalInfoRepository.findById(id);

        if (optionalPersonalInfo.isPresent()) {
            PersonalInfo personalInfo = optionalPersonalInfo.get();
            personalInfo.setName(personalInfoDetails.getName());
            personalInfo.setPhone(personalInfoDetails.getPhone());
            personalInfo.setLocation(personalInfoDetails.getLocation());
            personalInfo.setEmail(personalInfoDetails.getEmail());
            personalInfo.setLinkedinUrl(personalInfoDetails.getLinkedinUrl());

            return ResponseEntity.ok(personalInfoRepository.save(personalInfo));
        }

        return ResponseEntity.notFound().build();
    }

    // Delete personal info (use with caution - this removes your header!)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonalInfo(@PathVariable Long id) {
        if (personalInfoRepository.existsById(id)) {
            personalInfoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Check if personal info exists
    @GetMapping("/exists")
    public ResponseEntity<Boolean> personalInfoExists() {
        return ResponseEntity.ok(personalInfoRepository.exists());
    }
}
