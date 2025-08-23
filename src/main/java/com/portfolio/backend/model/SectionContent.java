package com.portfolio.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "section_content")
public class SectionContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    private String subtitle;
    private String duration;
    private String location;

    @Column(length = 2000)
    private String description;

    // For bullet points
    @ElementCollection
    @CollectionTable(name = "section_content_bullets", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "bullet_point", length = 1000)
    private List<String> bulletPoints;

    private String primaryUrl;
    private String secondaryUrl;

    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters

    public SectionContent() {}

    public SectionContent(Long id, Section section, String title, String subtitle, String duration, String location, String description, List<String> bulletPoints, String primaryUrl, String secondaryUrl, Integer orderIndex, Boolean isVisible) {
        this.id = id;
        this.section = section;
        this.title = title;
        this.subtitle = subtitle;
        this.duration = duration;
        this.location = location;
        this.description = description;
        this.bulletPoints = bulletPoints;
        this.primaryUrl = primaryUrl;
        this.secondaryUrl = secondaryUrl;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(String primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public String getSecondaryUrl() {
        return secondaryUrl;
    }

    public void setSecondaryUrl(String secondaryUrl) {
        this.secondaryUrl = secondaryUrl;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
