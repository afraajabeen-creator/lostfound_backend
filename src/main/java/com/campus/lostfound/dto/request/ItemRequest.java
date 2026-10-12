package com.campus.lostfound.dto.request;

import com.campus.lostfound.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO for creating or updating a lost/found item listing.
 */
public class ItemRequest {

    @NotNull(message = "Item type (LOST or FOUND) is required")
    private ItemType type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    private String imageUrl;

    public ItemRequest() {
    }

    public ItemRequest(ItemType type, String title, String category, String description,
                       String location, LocalDate incidentDate, String imageUrl) {
        this.type = type;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.incidentDate = incidentDate;
        this.imageUrl = imageUrl;
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
}
