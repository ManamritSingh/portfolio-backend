package com.portfolio.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // "Technical Skills", "Other Skills", "Hobbies and Passion"

    @Column(nullable = false, length = 1000)
    private String skillsList; // Comma-separated skills

    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters
    public Skill() {}

    public Skill(Long id, String category, String skillsList, Integer orderIndex, Boolean isVisible) {
        this.id = id;
        this.category = category;
        this.skillsList = skillsList;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSkillsList() {
        return skillsList;
    }

    public void setSkillsList(String skillsList) {
        this.skillsList = skillsList;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }
}
