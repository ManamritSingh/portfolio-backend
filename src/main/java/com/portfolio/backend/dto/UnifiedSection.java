package com.portfolio.backend.dto;

import java.time.LocalDateTime;

public class UnifiedSection {
    private Long id; // null for virtual sections
    private String sectionName;
    private String description;
    private Integer orderIndex;
    private Boolean visible;
    private String sectionType; // "personal-info", "experience", "education", "dynamic"
    private Boolean isDynamic; // false for fixed sections, true for custom sections
    private Integer contentCount;
    private LocalDateTime createdAt;
    private String route; // Frontend route for navigation

    // Constructors
    public UnifiedSection() {}

    public UnifiedSection(Long id, String sectionName, String description, Integer orderIndex,
                          Boolean visible, String sectionType, Boolean isDynamic, Integer contentCount,
                          LocalDateTime createdAt, String route) {
        this.id = id;
        this.sectionName = sectionName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.visible = visible;
        this.sectionType = sectionType;
        this.isDynamic = isDynamic;
        this.contentCount = contentCount;
        this.createdAt = createdAt;
        this.route = route;
    }

    // Static factory methods for fixed sections
    public static UnifiedSection createFixedSection(String name, String description, Integer order,
                                                    String type, String route, Integer count) {
        return new UnifiedSection(
                null, // No real ID for fixed sections
                name,
                description,
                order,
                true, // Always visible for now
                type,
                false, // Fixed section
                count,
                LocalDateTime.now(),
                route
        );
    }

    public static UnifiedSection fromDynamicSection(com.portfolio.backend.model.Section section,
                                                    Integer contentCount, String route) {
        return new UnifiedSection(
                section.getId(),
                section.getSectionName(),
                section.getDescription(),
                section.getOrderIndex(),
                section.getVisible(),
                "dynamic",
                true, // Dynamic section
                contentCount,
                section.getCreatedAt(),
                route
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(Boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
