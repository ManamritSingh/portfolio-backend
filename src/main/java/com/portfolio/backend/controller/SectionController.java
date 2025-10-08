package com.portfolio.backend.controller;

import com.portfolio.backend.dto.UnifiedSection;
import com.portfolio.backend.model.Section;
import com.portfolio.backend.model.SectionSetting;
import com.portfolio.backend.repository.SectionRepository;
import com.portfolio.backend.repository.SectionSettingRepository;
import com.portfolio.backend.service.SectionAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
// Removed class-level @RequestMapping and @CrossOrigin for global config
public class SectionController {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionAdapterService sectionAdapterService;

    @Autowired
    private SectionSettingRepository sectionSettingRepository;

    // =======================================================
    // PUBLIC ENDPOINTS - No authentication required
    // =======================================================

    // --- Public Dynamic Section Endpoints ---
    @GetMapping("/api/public/sections")
    public List<Section> getVisibleSections() {
        return sectionRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/sections/name/{sectionName}")
    public ResponseEntity<Section> getSectionByName(@PathVariable String sectionName) {
        Optional<Section> section = sectionRepository.findBySectionNameIgnoreCase(sectionName);
        return section.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/public/sections/search/{keyword}")
    public List<Section> searchSections(@PathVariable String keyword) {
        return sectionRepository.findBySectionNameContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    @GetMapping("/api/public/sections/exists/{sectionName}")
    public ResponseEntity<Boolean> sectionExists(@PathVariable String sectionName) {
        return ResponseEntity.ok(sectionRepository.existsBySectionNameIgnoreCase(sectionName));
    }

    // --- Public Unified Section Endpoints ---
    @GetMapping("/api/public/sections/unified")
    public List<UnifiedSection> getVisibleSectionsUnified() {
        // This is the primary endpoint for rendering your public resume sections
        return sectionAdapterService.getVisibleSectionsUnified();
    }


    // =======================================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // =======================================================

    // --- Admin Dynamic Section Endpoints ---
    @GetMapping("/api/admin/sections")
    public List<Section> getAllSections() {
        return sectionRepository.findAllByOrderByOrderIndexAsc();
    }

    @GetMapping("/api/admin/sections/{id}")
    public ResponseEntity<Section> getSection(@PathVariable Long id) {
        Optional<Section> section = sectionRepository.findById(id);
        return section.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/sections")
    public ResponseEntity<Section> createSection(@RequestBody Section section) {
        if (sectionRepository.existsBySectionNameIgnoreCase(section.getSectionName())) {
            return ResponseEntity.badRequest().build();
        }
        if (section.getOrderIndex() == null) {
            section.setOrderIndex(sectionRepository.getMaxOrderIndex() + 1);
        }
        section.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(sectionRepository.save(section));
    }

    @PutMapping("/api/admin/sections/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable Long id, @RequestBody Section sectionDetails) {
        return sectionRepository.findById(id).map(section -> {
            if (!section.getSectionName().equalsIgnoreCase(sectionDetails.getSectionName()) &&
                    sectionRepository.existsBySectionNameIgnoreCase(sectionDetails.getSectionName())) {
                return ResponseEntity.badRequest().<Section>build();
            }
            section.setSectionName(sectionDetails.getSectionName());
            section.setDescription(sectionDetails.getDescription());
            section.setOrderIndex(sectionDetails.getOrderIndex());
            section.setVisible(sectionDetails.getVisible());
            return ResponseEntity.ok(sectionRepository.save(section));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/sections/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable Long id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- Admin Unified Section Endpoints ---
    @GetMapping("/api/admin/sections/unified")
    public List<UnifiedSection> getAllSectionsUnified() {
        // Gets all sections (visible and hidden) for the admin reorder list
        return sectionAdapterService.getAllSectionsUnified();
    }

    @PutMapping("/api/admin/sections/unified/reorder")
    public ResponseEntity<?> reorderUnifiedSections(@RequestBody List<UnifiedSection> sections) {
        try {
            sectionAdapterService.reorderUnifiedSections(sections);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log the error server-side
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/api/admin/sections/unified/{sectionType}/visibility")
    public ResponseEntity<?> toggleFixedSectionVisibility(@PathVariable String sectionType, @RequestBody boolean isVisible) {
        try {
            sectionAdapterService.toggleFixedSectionVisibility(sectionType, isVisible);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log the error server-side
            return ResponseEntity.internalServerError().build();
        }
    }
}
