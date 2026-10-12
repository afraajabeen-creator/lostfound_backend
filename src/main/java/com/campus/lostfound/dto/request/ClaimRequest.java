package com.campus.lostfound.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for student submitting an ownership claim on a found item.
 */
public class ClaimRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Proof description is required (e.g., serial number, unique marks, color details)")
    private String proofDescription;

    private String proofImageUrl;

    public ClaimRequest() {
    }

    public ClaimRequest(Long itemId, String proofDescription, String proofImageUrl) {
        this.itemId = itemId;
        this.proofDescription = proofDescription;
        this.proofImageUrl = proofImageUrl;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }

    public String getProofImageUrl() {
        return proofImageUrl;
    }

    public void setProofImageUrl(String proofImageUrl) {
        this.proofImageUrl = proofImageUrl;
    }
}
