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
@RequestMapping("/api/sections")
@CrossOrigin(origins = "http://localhost:5173")
public class SectionController {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionAdapterService sectionAdapterService; // Fixed naming

    @Autowired
    private SectionSettingRepository sectionSettingRepository;

    // ========== ORIGINAL DYNAMIC SECTION ENDPOINTS ==========

    // Get all visible dynamic sections (for public resume)
    @GetMapping("/public")
    public List<Section> getVisibleSections() {
        return sectionRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all dynamic sections (for admin)
    @GetMapping
    public List<Section> getAllSections() {
        return sectionRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single dynamic section
    @GetMapping("/{id}")
    public ResponseEntity<Section> getSection(@PathVariable Long id) {
        Optional<Section> section = sectionRepository.findById(id);
        return section.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get section by name
    @GetMapping("/name/{sectionName}")
    public ResponseEntity<Section> getSectionByName(@PathVariable String sectionName) {
        Optional<Section> section = sectionRepository.findBySectionNameIgnoreCase(sectionName);
        return section.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search sections by keyword
    @GetMapping("/search/{keyword}")
    public List<Section> searchSections(@PathVariable String keyword) {
        return sectionRepository.findBySectionNameContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(keyword);
    }

    // Get recent sections (created in last 30 days)
    @GetMapping("/recent")
    public List<Section> getRecentSections() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return sectionRepository.findRecentSections(thirtyDaysAgo);
    }

    // Check if section name exists
    @GetMapping("/exists/{sectionName}")
    public ResponseEntity<Boolean> sectionExists(@PathVariable String sectionName) {
        return ResponseEntity.ok(sectionRepository.existsBySectionNameIgnoreCase(sectionName));
    }

    // Create new dynamic section
    @PostMapping
    public ResponseEntity<Section> createSection(@RequestBody Section section) {
        // Check if section name already exists
        if (sectionRepository.existsBySectionNameIgnoreCase(section.getSectionName())) {
            return ResponseEntity.badRequest().build();
        }

        // Auto-set order index if not provided
        if (section.getOrderIndex() == null) {
            section.setOrderIndex(sectionRepository.getMaxOrderIndex() + 1);
        }

        // Set creation time
        section.setCreatedAt(LocalDateTime.now());

        Section savedSection = sectionRepository.save(section);
        return ResponseEntity.ok(savedSection);
    }

    // Update dynamic section
    @PutMapping("/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable Long id,
                                                 @RequestBody Section sectionDetails) {
        Optional<Section> optionalSection = sectionRepository.findById(id);

        if (optionalSection.isPresent()) {
            Section section = optionalSection.get();

            // Check if new section name conflicts with existing ones (excluding current)
            if (!section.getSectionName().equalsIgnoreCase(sectionDetails.getSectionName()) &&
                    sectionRepository.existsBySectionNameIgnoreCase(sectionDetails.getSectionName())) {
                return ResponseEntity.badRequest().build();
            }

            section.setSectionName(sectionDetails.getSectionName());
            section.setDescription(sectionDetails.getDescription());
            section.setOrderIndex(sectionDetails.getOrderIndex());
            section.setVisible(sectionDetails.getVisible());

            return ResponseEntity.ok(sectionRepository.save(section));
        }

        return ResponseEntity.notFound().build();
    }

    // Reorder dynamic sections only
    @PutMapping("/reorder")
    public ResponseEntity<List<Section>> reorderSections(@RequestBody List<Section> sections) {
        // Update order indices
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            section.setOrderIndex(i + 1);
        }

        List<Section> savedSections = sectionRepository.saveAll(sections);
        return ResponseEntity.ok(savedSections);
    }

    // Delete dynamic section
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable Long id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ========== UNIFIED SECTION ENDPOINTS (NEW) ==========

    // Get all sections unified (fixed + dynamic)
    @GetMapping("/unified")
    public List<UnifiedSection> getAllSectionsUnified() {
        return sectionAdapterService.getAllSectionsUnified();
    }

    // Get visible sections unified (for public resume)
    @GetMapping("/unified/public")
    public List<UnifiedSection> getVisibleSectionsUnified() {
        return sectionAdapterService.getVisibleSectionsUnified();
    }

    // Reorder all sections (fixed + dynamic)
    @PutMapping("/unified/reorder")
    public ResponseEntity<?> reorderUnifiedSections(@RequestBody List<UnifiedSection> sections) {
        try {
            // Add debug logging
            System.out.println("Received sections for reorder:");
            for (int i = 0; i < sections.size(); i++) {
                UnifiedSection section = sections.get(i);
                System.out.println("Position " + (i+1) + ": " + section.getSectionName() +
                        " (isDynamic: " + section.getIsDynamic() + ")");
            }

            for (int i = 0; i < sections.size(); i++) {
                UnifiedSection section = sections.get(i);
                int newPosition = i + 1; // Position in unified list

                if (section.getIsDynamic()) {
                    // CUSTOM SECTION: Save the actual position in unified list
                    if (section.getId() != null) {
                        Optional<Section> dynamicSection = sectionRepository.findById(section.getId());
                        if (dynamicSection.isPresent()) {
                            Section sec = dynamicSection.get();
                            sec.setOrderIndex(newPosition); // Store actual position
                            sectionRepository.save(sec);
                            System.out.println("Updated custom section " + sec.getSectionName() +
                                    " to position " + newPosition);
                        }
                    }
                } else {
                    // FIXED SECTION: Save to section_settings
                    Optional<SectionSetting> setting = sectionSettingRepository.findBySectionType(section.getSectionType());
                    if (setting.isPresent()) {
                        SectionSetting ss = setting.get();
                        ss.setDisplayOrder(newPosition);
                        sectionSettingRepository.save(ss);
                        System.out.println("Updated fixed section " + section.getSectionName() +
                                " to position " + newPosition);
                    } else {
                        // Create new setting
                        SectionSetting newSetting = new SectionSetting(section.getSectionType(), newPosition, section.getVisible());
                        sectionSettingRepository.save(newSetting);
                        System.out.println("Created new setting for " + section.getSectionName() +
                                " at position " + newPosition);
                    }
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error reordering unified sections: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Toggle fixed section visibility
    @PutMapping("/unified/{sectionType}/visibility")
    public ResponseEntity<?> toggleFixedSectionVisibility(@PathVariable String sectionType) {
        try {
            Optional<SectionSetting> setting = sectionSettingRepository.findBySectionType(sectionType);
            if (setting.isPresent()) {
                SectionSetting ss = setting.get();
                ss.setIsVisible(!ss.getIsVisible());
                sectionSettingRepository.save(ss);
            } else {
                // Create new setting with opposite visibility
                SectionSetting newSetting = new SectionSetting(sectionType, 1, false);
                sectionSettingRepository.save(newSetting);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error toggling section visibility: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
