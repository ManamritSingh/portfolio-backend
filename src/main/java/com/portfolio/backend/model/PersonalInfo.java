package com.portfolio.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "personal_info")
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String location;
    private String email;
    private String linkedinUrl;

    // Constructors, getters, setters
    public PersonalInfo() {}

    public PersonalInfo(Long id, String name, String phone, String location, String email, String linkedinUrl) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
}
