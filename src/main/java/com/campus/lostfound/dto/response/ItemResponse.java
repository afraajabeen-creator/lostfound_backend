package com.campus.lostfound.dto.response;

import com.campus.lostfound.entity.Item;
import com.campus.lostfound.enums.ItemStatus;
import com.campus.lostfound.enums.ItemType;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing an item listing in API responses.
 */
public class ItemResponse {

    private Long id;
    private ItemType type;
    private String title;
    private String category;
    private String description;
    private String location;
    private LocalDate incidentDate;
    private String imageUrl;
    private ItemStatus status;
    private UserSummaryDto reporter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponse() {
    }

    public ItemResponse(Item item) {
        if (item != null) {
            this.id = item.getId();
            this.type = item.getType();
            this.title = item.getTitle();
            this.category = item.getCategory();
            this.description = item.getDescription();
            this.location = item.getLocation();
            this.incidentDate = item.getIncidentDate();
            this.imageUrl = item.getImageUrl();
            this.status = item.getStatus();
            this.reporter = UserSummaryDto.fromEntity(item.getReporter());
            this.createdAt = item.getCreatedAt();
            this.updatedAt = item.getUpdatedAt();
        }
    }

    public static ItemResponse fromEntity(Item item) {
        return new ItemResponse(item);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String location() {
        return location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public UserSummaryDto getReporter() {
        return reporter;
    }

    public void setReporter(UserSummaryDto reporter) {
        this.reporter = reporter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
