package com.portfolio.backend.service;

import com.portfolio.backend.dto.UnifiedSection;
import com.portfolio.backend.model.Section;
import com.portfolio.backend.model.SectionSetting;
import com.portfolio.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Get saved orders from section_settings table
        Map<String, Integer> savedOrders = getSavedFixedSectionOrders();

        // Add fixed sections using saved orders
        allSections.add(createPersonalInfoSection(savedOrders.getOrDefault("personal-info", 1)));
        allSections.add(createExperienceSection(savedOrders.getOrDefault("experience", 2)));
        allSections.add(createEducationSection(savedOrders.getOrDefault("education", 3)));
        allSections.add(createSkillsSection(savedOrders.getOrDefault("skills", 4)));
        allSections.add(createProjectsSection(savedOrders.getOrDefault("projects", 5)));
        allSections.add(createCertificationsSection(savedOrders.getOrDefault("certifications", 6)));
        allSections.add(createLeadershipSection(savedOrders.getOrDefault("leadership", 7)));

        // Add dynamic sections - use their SAVED order directly (no offset)
        List<Section> dynamicSections = sectionRepository.findAllByOrderByOrderIndexAsc();
        for (Section section : dynamicSections) {
            Integer contentCount = Math.toIntExact(sectionContentRepository.countBySectionIdAndIsVisibleTrue(section.getId()));
            String route = "/admin/sections/" + section.getId() + "/content";

            UnifiedSection unifiedSection = UnifiedSection.fromDynamicSection(section, contentCount, route);
            // Use the saved order directly - no offset adjustment needed for display
            unifiedSection.setOrderIndex(section.getOrderIndex());

            allSections.add(unifiedSection);
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

    // Helper method to get saved fixed section orders
    private Map<String, Integer> getSavedFixedSectionOrders() {
        List<SectionSetting> settings = sectionSettingRepository.findAllByOrderByDisplayOrderAsc();
        Map<String, Integer> orderMap = new HashMap<>();

        for (SectionSetting setting : settings) {
            orderMap.put(setting.getSectionType(), setting.getDisplayOrder());
        }

        return orderMap;
    }

    // Private helper methods for creating fixed sections
    private UnifiedSection createPersonalInfoSection(int order) {
        return UnifiedSection.createFixedSection(
                "Personal Information",
                "Contact details and basic information",
                order,
                "personal-info",
                "/admin/personal",
                1
        );
    }

    private UnifiedSection createExperienceSection(int order) {
        long count = experienceRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Experience",
                "Work experience and professional history",
                order,
                "experience",
                "/admin/experience",
                Math.toIntExact(count)
        );
    }

    private UnifiedSection createEducationSection(int order) {
        long count = educationRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Education",
                "Academic background and qualifications",
                order,
                "education",
                "/admin/education",
                Math.toIntExact(count)
        );
    }

    private UnifiedSection createSkillsSection(int order) {
        long count = skillRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Skills",
                "Technical skills and competencies",
                order,
                "skills",
                "/admin/skills",
                Math.toIntExact(count)
        );
    }

    private UnifiedSection createProjectsSection(int order) {
        long count = projectRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Projects",
                "Portfolio projects and developments",
                order,
                "projects",
                "/admin/projects",
                Math.toIntExact(count)
        );
    }

    private UnifiedSection createCertificationsSection(int order) {
        long count = certificationRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Certifications",
                "Professional certifications and credentials",
                order,
                "certifications",
                "/admin/certifications",
                Math.toIntExact(count)
        );
    }

    private UnifiedSection createLeadershipSection(int order) {
        long count = leadershipRepository.countByIsVisibleTrue();
        return UnifiedSection.createFixedSection(
                "Leadership & Involvement",
                "Leadership roles and community involvement",
                order,
                "leadership",
                "/admin/leadership",
                Math.toIntExact(count)
        );
    }
}
