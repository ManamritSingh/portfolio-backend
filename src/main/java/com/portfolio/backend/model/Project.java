package com.portfolio.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String subtitle; // Short description line

    @ElementCollection
    @CollectionTable(name = "project_bullets", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "bullet_point", length = 1000)
    private List<String> bulletPoints;

    private String Url;


    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters


    public Project() {
    }

    public Project(Long id, String title, String subtitle, List<String> bulletPoints, String url, Integer orderIndex, Boolean isVisible) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.bulletPoints = bulletPoints;
        Url = url;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
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
