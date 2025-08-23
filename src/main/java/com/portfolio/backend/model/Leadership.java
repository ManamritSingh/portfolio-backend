package com.portfolio.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leadership")
public class Leadership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters


    public Leadership() {
    }

    public Leadership(Long id, String description, Integer orderIndex, Boolean isVisible) {
        this.id = id;
        this.description = description;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }
}
