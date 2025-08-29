package com.portfolio.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "section_settings")
public class SectionSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sectionType; // "personal-info", "experience", etc.

    private Integer displayOrder;
    private Boolean isVisible = true;

    // Constructors, getters, setters
    public SectionSetting() {}

    public SectionSetting(String sectionType, Integer displayOrder, Boolean isVisible) {
        this.sectionType = sectionType;
        this.displayOrder = displayOrder;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
}
