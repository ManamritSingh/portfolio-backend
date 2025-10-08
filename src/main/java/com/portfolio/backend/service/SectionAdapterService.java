package com.portfolio.backend.service;

import com.portfolio.backend.dto.UnifiedSection;
import com.portfolio.backend.model.Section;
import com.portfolio.backend.model.SectionSetting;
import com.portfolio.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectionAdapterService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private LeadershipRepository leadershipRepository;

    @Autowired
    private SectionContentRepository sectionContentRepository;

    @Autowired
    private SectionSettingRepository sectionSettingRepository;

    public List<UnifiedSection> getAllSectionsUnified() {
        List<UnifiedSection> allSections = new ArrayList<>();

        // Get saved settings from the database
        Map<String, SectionSetting> settingsMap = sectionSettingRepository.findAll().stream()
                .collect(Collectors.toMap(SectionSetting::getSectionType, setting -> setting));

        // Add fixed sections using saved or default settings
        allSections.add(createFixedSectionFromSetting("personal-info", "Personal Information", "Contact details and basic information", "/admin/personal", 1, 1L, settingsMap));
        allSections.add(createFixedSectionFromSetting("experience", "Experience", "Work experience and professional history", "/admin/experience", 2, experienceRepository.countByIsVisibleTrue(), settingsMap));
        allSections.add(createFixedSectionFromSetting("education", "Education", "Academic background and qualifications", "/admin/education", 3, educationRepository.countByIsVisibleTrue(), settingsMap));
        allSections.add(createFixedSectionFromSetting("skills", "Skills", "Technical skills and competencies", "/admin/skills", 4, skillRepository.countByIsVisibleTrue(), settingsMap));
        allSections.add(createFixedSectionFromSetting("projects", "Projects", "Portfolio projects and developments", "/admin/projects", 5, projectRepository.countByIsVisibleTrue(), settingsMap));
        allSections.add(createFixedSectionFromSetting("certifications", "Certifications", "Professional certifications and credentials", "/admin/certifications", 6, certificationRepository.countByIsVisibleTrue(), settingsMap));
        allSections.add(createFixedSectionFromSetting("leadership", "Leadership & Involvement", "Leadership roles and community involvement", "/admin/leadership", 7, leadershipRepository.countByIsVisibleTrue(), settingsMap));


        // Add dynamic sections
        List<Section> dynamicSections = sectionRepository.findAll();
        for (Section section : dynamicSections) {
            Integer contentCount = Math.toIntExact(sectionContentRepository.countBySectionIdAndIsVisibleTrue(section.getId()));
            allSections.add(UnifiedSection.fromDynamicSection(section, contentCount));
        }

        return allSections.stream()
                .sorted(Comparator.comparing(UnifiedSection::getOrderIndex))
                .collect(Collectors.toList());
    }

    public List<UnifiedSection> getVisibleSectionsUnified() {
        return getAllSectionsUnified().stream()
                .filter(UnifiedSection::getVisible)
                .collect(Collectors.toList());
    }

    // =======================================================
    // NEW METHODS TO BE CALLED FROM THE CONTROLLER
    // =======================================================

    @Transactional
    public void reorderUnifiedSections(List<UnifiedSection> sections) {
        for (int i = 0; i < sections.size(); i++) {
            UnifiedSection section = sections.get(i);
            int newPosition = i + 1; // Position is 1-based

            if (section.getDynamic()) { // Changed from getIsDynamic() to isDynamic()
                // It's a dynamic, custom section...
                if (section.getId() != null) {
                    sectionRepository.findById(section.getId()).ifPresent(dynamicSection -> {
                        dynamicSection.setOrderIndex(newPosition);
                        sectionRepository.save(dynamicSection);
                    });
                }
            } else {
                // It's a fixed, hard-coded section. Update or create the SectionSetting.
                SectionSetting setting = sectionSettingRepository.findBySectionType(section.getSectionType())
                        .orElse(new SectionSetting(section.getSectionType(), newPosition, section.getVisible()));
                setting.setDisplayOrder(newPosition);
                sectionSettingRepository.save(setting);
            }
        }
    }

    @Transactional
    public void toggleFixedSectionVisibility(String sectionType, boolean isVisible) {
        // Find the setting for the fixed section
        SectionSetting setting = sectionSettingRepository.findBySectionType(sectionType)
                .orElse(new SectionSetting(sectionType, 99, isVisible)); // Create if not exists, default order to last

        setting.setIsVisible(isVisible);
        sectionSettingRepository.save(setting);
    }

    // =======================================================
    // HELPER METHODS
    // =======================================================

    private UnifiedSection createFixedSectionFromSetting(String type, String defaultName, String defaultDesc, String route, int defaultOrder, Long count, Map<String, SectionSetting> settingsMap) {
        SectionSetting setting = settingsMap.get(type);
        if (setting != null) {
            // Use saved setting
            return UnifiedSection.createFixedSection(defaultName, defaultDesc, setting.getDisplayOrder(), type, route, Math.toIntExact(count), setting.getIsVisible());
        } else {
            // Use default values if no setting is saved yet
            return UnifiedSection.createFixedSection(defaultName, defaultDesc, defaultOrder, type, route, Math.toIntExact(count), true); // Default to visible
        }
    }
}
