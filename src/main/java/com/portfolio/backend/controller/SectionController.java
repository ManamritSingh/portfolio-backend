package com.portfolio.backend.controller;

import com.portfolio.backend.model.Section;
import com.portfolio.backend.repository.SectionRepository;
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

    // Get all visible sections (for public resume)
    @GetMapping("/public")
    public List<Section> getVisibleSections() {
        return sectionRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get all sections (for admin)
    @GetMapping
    public List<Section> getAllSections() {
        return sectionRepository.findAllByOrderByOrderIndexAsc();
    }

    // Get single section
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

    // Create new section
    @PostMapping
    public ResponseEntity<Section> createSection(@RequestBody Section section) {
        // Check if section name already exists
        if (sectionRepository.existsBySectionNameIgnoreCase(section.getSectionName())) {
            return ResponseEntity.badRequest().build(); // Section name already exists
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

    // Update section
    @PutMapping("/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable Long id,
                                                 @RequestBody Section sectionDetails) {
        Optional<Section> optionalSection = sectionRepository.findById(id);

        if (optionalSection.isPresent()) {
            Section section = optionalSection.get();

            // Check if new section name conflicts with existing ones (excluding current)
            if (!section.getSectionName().equalsIgnoreCase(sectionDetails.getSectionName()) &&
                    sectionRepository.existsBySectionNameIgnoreCase(sectionDetails.getSectionName())) {
                return ResponseEntity.badRequest().build(); // Section name already exists
            }

            section.setSectionName(sectionDetails.getSectionName());
            section.setDescription(sectionDetails.getDescription());
            section.setOrderIndex(sectionDetails.getOrderIndex());
            section.setVisible(sectionDetails.getVisible());
            // Don't update createdAt - keep original

            return ResponseEntity.ok(sectionRepository.save(section));
        }

        return ResponseEntity.notFound().build();
    }

    // Update section order indices (for drag-and-drop reordering)
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

    // Delete section
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable Long id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
