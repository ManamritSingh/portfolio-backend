package com.portfolio.backend.dto;

import com.portfolio.backend.model.Section;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifiedSection {

    // ... (all your fields and the main constructor remain the same) ...
    private Long id;
    private String sectionName;
    private String description;
    private Integer orderIndex;
    private Boolean isVisible;
    private String sectionType;
    private Boolean isDynamic;
    private Integer itemCount;
    private String route;
    private LocalDateTime createdAt;

    public UnifiedSection() {}

    public UnifiedSection(Long id, String sectionName, String description, Integer orderIndex,
                          Boolean visible, String sectionType, Boolean isDynamic, Integer contentCount,
                          LocalDateTime createdAt, String route) {
        this.id = id;
        this.sectionName = sectionName;
        this.description = description;
        this.orderIndex = orderIndex;
        this.isVisible = visible;
        this.sectionType = sectionType;
        this.isDynamic = isDynamic;
        this.itemCount = contentCount;
        this.createdAt = createdAt;
        this.route = route;
    }


    // --- THE FIX IS HERE ---

    /**
     * NEW: Overloaded method that only requires 2 arguments.
     * This is the version your service will now call.
     */
    public static UnifiedSection fromDynamicSection(Section section, Integer contentCount) {
        // It constructs the route internally...
        String route = "/admin/sections/" + section.getId() + "/content";
        // ...and then calls the original 3-argument method.
        return fromDynamicSection(section, contentCount, route);
    }

    /**
     * ORIGINAL: The 3-argument factory method remains unchanged.
     */
    public static UnifiedSection fromDynamicSection(Section section,
                                                    Integer contentCount, String route) {
        return new UnifiedSection(
                section.getId(),
                section.getSectionName(),
                section.getDescription(),
                section.getOrderIndex(),
                section.getVisible(),
                "dynamic",
                true,
                contentCount,
                section.getCreatedAt(),
                route
        );
    }

    /**
     * Factory for fixed sections (already correct).
     */
    public static UnifiedSection createFixedSection(String name, String description, Integer order,
                                                    String type, String route, Integer count, Boolean visible) {
        return new UnifiedSection(null, name, description, order, visible, type, false, count, null, route);
    }

    // --- Getters and Setters ... ---

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
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Boolean getDynamic() {
        return isDynamic;
    }

    public void setDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
