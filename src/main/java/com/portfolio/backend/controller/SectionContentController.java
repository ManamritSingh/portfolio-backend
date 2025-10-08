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
// Removed class-level annotations
public class SectionContentController {

    @Autowired
    private SectionContentRepository sectionContentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/sections/{sectionId}/content")
    public List<SectionContent> getVisibleContentForSection(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdAndIsVisibleTrueOrderByOrderIndexAsc(sectionId);
    }

    @GetMapping("/api/public/sections/{sectionId}/content/with-urls")
    public List<SectionContent> getContentWithUrls(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdAndPrimaryUrlIsNotNullAndIsVisibleTrueOrderByOrderIndexAsc(sectionId);
    }

    @GetMapping("/api/public/sections/{sectionId}/content/with-bullets")
    public List<SectionContent> getContentWithBulletPoints(@PathVariable Long sectionId) {
        return sectionContentRepository.findContentWithBulletPoints(sectionId);
    }

    @GetMapping("/api/public/sections/{sectionId}/content/search/title/{keyword}")
    public List<SectionContent> searchContentByTitle(@PathVariable Long sectionId, @PathVariable String keyword) {
        return sectionContentRepository.findBySectionIdAndTitleContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(sectionId, keyword);
    }

    @GetMapping("/api/public/sections/{sectionId}/content/search/description/{keyword}")
    public List<SectionContent> searchContentByDescription(@PathVariable Long sectionId, @PathVariable String keyword) {
        return sectionContentRepository.findBySectionIdAndDescriptionContainingIgnoreCaseAndIsVisibleTrueOrderByOrderIndexAsc(sectionId, keyword);
    }

    @GetMapping("/api/public/sections/{sectionId}/content/count")
    public ResponseEntity<Long> getContentCount(@PathVariable Long sectionId) {
        long count = sectionContentRepository.countBySectionIdAndIsVisibleTrue(sectionId);
        return ResponseEntity.ok(count);
    }

    // ===========================================
    // ADMIN ENDPOINTS - Requires ROLE_ADMIN
    // ===========================================

    @GetMapping("/api/admin/sections/{sectionId}/content")
    public List<SectionContent> getAllContentForSection(@PathVariable Long sectionId) {
        return sectionContentRepository.findBySectionIdOrderByOrderIndexAsc(sectionId);
    }

    @PostMapping("/api/admin/sections/{sectionId}/content")
    public ResponseEntity<SectionContent> createContent(@PathVariable Long sectionId, @RequestBody SectionContent content) {
        if (!sectionRepository.existsById(sectionId)) {
            return ResponseEntity.badRequest().build();
        }

        content.setSection(sectionRepository.findById(sectionId).get());

        if (content.getOrderIndex() == null) {
            content.setOrderIndex(sectionContentRepository.getMaxOrderIndexForSection(sectionId) + 1);
        }

        SectionContent savedContent = sectionContentRepository.save(content);
        return ResponseEntity.ok(savedContent);
    }

    @PutMapping("/api/admin/sections/{sectionId}/content/{contentId}")
    public ResponseEntity<SectionContent> updateContent(@PathVariable Long sectionId, @PathVariable Long contentId, @RequestBody SectionContent contentDetails) {
        Optional<SectionContent> optionalContent = sectionContentRepository.findById(contentId);

        if (optionalContent.isPresent()) {
            SectionContent content = optionalContent.get();

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

    @DeleteMapping("/api/admin/sections/{sectionId}/content/{contentId}")
    public ResponseEntity<?> deleteContent(@PathVariable Long sectionId, @PathVariable Long contentId) {
        Optional<SectionContent> optionalContent = sectionContentRepository.findById(contentId);
        if (optionalContent.isPresent()) {
            SectionContent content = optionalContent.get();
            if (!content.getSection().getId().equals(sectionId)) {
                return ResponseEntity.badRequest().build();
            }
            sectionContentRepository.deleteById(contentId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
@RestController
// Removed class-level annotations
class GlobalSectionContentController {

    @Autowired
    private SectionContentRepository sectionContentRepository;

    // ===========================================
    // PUBLIC ENDPOINTS - No authentication required
    // ===========================================

    @GetMapping("/api/public/section-content/{id}")
    public ResponseEntity<SectionContent> getContent(@PathVariable Long id) {
        Optional<SectionContent> content = sectionContentRepository.findById(id);
        return content.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/public/section-content/all")
    public List<SectionContent> getAllVisibleContent() {
        return sectionContentRepository.findByIsVisibleTrueOrderByOrderIndexAsc();
    }

    @GetMapping("/api/public/section-content/by-section/{sectionName}")
    public List<SectionContent> getContentBySectionName(@PathVariable String sectionName) {
        return sectionContentRepository.findBySectionNameAndVisible(sectionName);
    }
}
