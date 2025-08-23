package com.portfolio.backend.controller;

import com.portfolio.backend.model.SectionContent;
import com.portfolio.backend.repository.SectionContentRepository;
import com.portfolio.backend.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sections/{sectionId}/content")
@CrossOrigin(origins = "http://localhost:5173")
public class SectionContentController {

    @Autowired
    private SectionContentRepository sectionContentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    // Get all visible content for a section (for public resume)
    @GetMapping("/public")
    public List<SectionContent> getVisibleContentForSection(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdAndIsVisibleTrueOrderByOrderIndexAsc(sectionId);
    }

    // Get all content for a section (for admin)
    @GetMapping
    public List<SectionContent> getAllContentForSection(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdOrderByOrderIndexAsc(sectionId);
    }

    // Get content with URLs for a section
    @GetMapping("/with-urls")
    public List<SectionContent> getContentWithUrls(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdAndPrimaryUrlIsNotNullAndIsVisibleTrueOrderByOrderIndexAsc(sectionId);
    }

    // Get content with bullet points for a section
    @GetMapping("/with-bullets")
    public List<SectionContent> getContentWithBulletPoints(@PathVariable Long sectionId) {
        return sectionContentRepository.findContentWithBulletPoints(sectionId);
    }

    // Search content by title within a section
    @GetMapping("/search/title/{keyword}")
    public List<SectionContent> searchContentByTitle(@PathVariable Long sectionId, @PathVariable String keyword) {
        return sectionContentRepository.findBySectionIdAndTitleContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(sectionId, keyword);
    }

    // Search content by description within a section
    @GetMapping("/search/description/{keyword}")
    public List<SectionContent> searchContentByDescription(@PathVariable Long sectionId, @PathVariable String keyword) {
        return sectionContentRepository.findBySectionIdAndDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(sectionId, keyword);
    }

    // Count content items in section
    @GetMapping("/count")
    public ResponseEntity<Long> getContentCount(@PathVariable Long sectionId) {
        long count = sectionContentRepository.countBySectionIdAndIsVisibleTrue(sectionId);
        return ResponseEntity.ok(count);
    }

    // Create new content for section
    @PostMapping
    public ResponseEntity<SectionContent> createContent(@PathVariable Long sectionId,
                                                        @RequestBody SectionContent content) {
        // Verify section exists
        if (!sectionRepository.existsById(sectionId)) {
            return ResponseEntity.badRequest().build();
        }

        // Set the section
        content.setSection(sectionRepository.findById(sectionId).get());

        // Auto-set order index if not provided
        if (content.getOrderIndex() == null) {
            content.setOrderIndex(sectionContentRepository.getMaxOrderIndexForSection(sectionId) + 1);
        }

        SectionContent savedContent = sectionContentRepository.save(content);
        return ResponseEntity.ok(savedContent);
    }

    // Update content
    @PutMapping("/{contentId}")
    public ResponseEntity<SectionContent> updateContent(@PathVariable Long sectionId,
                                                        @PathVariable Long contentId,
                                                        @RequestBody SectionContent contentDetails) {
        Optional<SectionContent> optionalContent = sectionContentRepository.findById(contentId);

        if (optionalContent.isPresent()) {
            SectionContent content = optionalContent.get();

            // Verify content belongs to the correct section
            if (!content.getSection().getId().equals(sectionId)) {
                return ResponseEntity.badRequest().build();
            }

            content.setTitle(contentDetails.getTitle());
            content.setSubtitle(contentDetails.getSubtitle());
            content.setDuration(contentDetails.getDuration());
            content.setLocation(contentDetails.getLocation());
            content.setDescription(contentDetails.getDescription());
            content.setBulletPoints(contentDetails.getBulletPoints());
            content.setPrimaryUrl(contentDetails.getPrimaryUrl());
            content.setSecondaryUrl(contentDetails.getSecondaryUrl());
            content.setOrderIndex(contentDetails.getOrderIndex());
            content.setVisible(contentDetails.getVisible());

            return ResponseEntity.ok(sectionContentRepository.save(content));
        }

        return ResponseEntity.notFound().build();
    }

    // Delete content
    @DeleteMapping("/{contentId}")
    public ResponseEntity<?> deleteContent(@PathVariable Long sectionId, @PathVariable Long contentId) {
        Optional<SectionContent> optionalContent = sectionContentRepository.findById(contentId);

        if (optionalContent.isPresent()) {
            SectionContent content = optionalContent.get();

            // Verify content belongs to the correct section
            if (!content.getSection().getId().equals(sectionId)) {
                return ResponseEntity.badRequest().build();
            }

            sectionContentRepository.deleteById(contentId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}

// Additional controller for global section content operations
@RestController
@RequestMapping("/api/section-content")
@CrossOrigin(origins = "http://localhost:5173")
class GlobalSectionContentController {

    @Autowired
    private SectionContentRepository sectionContentRepository;

    // Get single content item by ID
    @GetMapping("/{id}")
    public ResponseEntity<SectionContent> getContent(@PathVariable Long id) {
        Optional<SectionContent> content = sectionContentRepository.findById(id);
        return content.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all visible content across all sections
    @GetMapping("/all/public")
    public List<SectionContent> getAllVisibleContent() {
        return sectionContentRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    // Get content by section name
    @GetMapping("/by-section/{sectionName}")
    public List<SectionContent> getContentBySectionName(@PathVariable String sectionName) {
        return sectionContentRepository.findBySectionNameAndVisible(sectionName);
    }
}
