package com.portfolio.backend.controller;

import com.portfolio.backend.model.Certification;
import com.portfolio.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "http://localhost:5173")
public class CertificationController {

    @Autowired
    private CertificationRepository certificationRepository;

    // Get all visible certifications (for public resume)
    @GetMapping("/public")
    public List<Certification> getVisibleCertifications() {
        return certificationRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all certifications (for admin)
    @GetMapping
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single certification
    @GetMapping("/{id}")
    public ResponseEntity<Certification> getCertification(@PathVariable Long id) {
        Optional<Certification> certification = certificationRepository.findById(id);
        return certification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get certification count
    @GetMapping("/count")
    public ResponseEntity<Long> getCertificationCount() {
        return ResponseEntity.ok(certificationRepository.countByIsVisibleTrue());
    }

    // Create new certification
    @PostMapping
    public ResponseEntity<Certification> createCertification(@RequestBody Certification certification) {
        // Auto-set order index if not provided
        if (certification.getOrderIndex() == null) {
            certification.setOrderIndex(certificationRepository.getMaxOrderIndex() + 1);
        }

        Certification savedCertification = certificationRepository.save(certification);
        return ResponseEntity.ok(savedCertification);
    }

    // Update certification
    @PutMapping("/{id}")
    public ResponseEntity<Certification> updateCertification(@PathVariable Long id, @RequestBody Certification certificationDetails) {
        Optional<Certification> optionalCertification = certificationRepository.findById(id);

        if (optionalCertification.isPresent()) {
            Certification certification = optionalCertification.get();
            certification.setName(certificationDetails.getName());
            certification.setIssuer(certificationDetails.getIssuer());
            certification.setDateObtained(certificationDetails.getDateObtained());
            certification.setUrl(certificationDetails.getUrl());
            certification.setOrderIndex(certificationDetails.getOrderIndex());
            certification.setVisible(certificationDetails.getVisible());

            return ResponseEntity.ok(certificationRepository.save(certification));
        }

        return ResponseEntity.notFound().build();
    }

    // Delete certification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertification(@PathVariable Long id) {
        if (certificationRepository.existsById(id)) {
            certificationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
