package com.portfolio.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "certifications")
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(length = 300)
    private String issuer;

    @Column(length = 100)
    private String dateObtained;

    @Column(length = 1000)
    private String url;

    private Integer orderIndex;
    private Boolean isVisible = true;

    // Constructors, getters, setters
    public Certification() {
    }

    public Certification(String name, String issuer, String dateObtained, String url, Integer orderIndex, Boolean isVisible) {
        this.name = name;
        this.issuer = issuer;
        this.dateObtained = dateObtained;
        this.url = url;
        this.orderIndex = orderIndex;
        this.isVisible = isVisible;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(String dateObtained) {
        this.dateObtained = dateObtained;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
