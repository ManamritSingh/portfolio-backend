package com.portfolio.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String position;

    private String duration;
    private String company;
    private String location;
    private String status; // "Current", null

    @ElementCollection
    @CollectionTable(name = "experience_bullets", joinColumns = @JoinColumn(name = "experience_id"))
    @Column(name = "bullet_point", length = 1000)
    private List<String> bulletPoints;

    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters

    public Experience() {}


    public Experience(Long id, String position, String duration, String company, String location, String status, List<String> bulletPoints, Integer orderIndex, Boolean isVisible) {
        this.id = id;
        this.position = position;
        this.duration = duration;
        this.company = company;
        this.location = location;
        this.status = status;
        this.bulletPoints = bulletPoints;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getBulletPoints() {
        return bulletPoints;
    }

    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
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
