package com.portfolio.backend.controller;

import com.portfolio.backend.model.Certification;
import com.portfolio.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Class-level annotations removed for specific path mapping and global CORS handling
public class CertificationController {

    @Autowired
    private CertificationRepository certificationRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/certifications")
    public List<Certification> getVisibleCertifications() {
        // Public endpoint returns only visible certifications
        return certificationRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/certifications/count")
    public ResponseEntity<Long> getCertificationCount() {
        // Public endpoint returning count of visible certifications
        return ResponseEntity.ok(certificationRepository.countByIsVisibleTrue());
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/certifications")
    public List<Certification> getAllCertifications() {
        // Admin can access all certifications (visible + hidden)
        return certificationRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/certifications/{id}")
    public ResponseEntity<Certification> getCertification(@PathVariable Long id) {
        // Admin can get single certification by ID
        Optional<Certification> certification = certificationRepository.findById(id);
        return certification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/certifications")
    public ResponseEntity<Certification> createCertification(@RequestBody Certification certification) {
        // Admin creates new certification;
        if (certification.getOrderIndex() == null) {
            certification.setOrderIndex(certificationRepository.getMaxOrderIndex() + 1);
        }

        Certification savedCertification = certificationRepository.save(certification);
        return ResponseEntity.ok(savedCertification);
    }

    @PutMapping("/api/admin/certifications/{id}")
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

    @DeleteMapping("/api/admin/certifications/{id}")
    public ResponseEntity<?> deleteCertification(@PathVariable Long id) {
        if (certificationRepository.existsById(id)) {
            certificationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
