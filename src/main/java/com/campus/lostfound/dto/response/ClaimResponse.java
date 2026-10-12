package com.campus.lostfound.dto.response;

import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.enums.ClaimStatus;
import java.time.LocalDateTime;

/**
 * DTO representing an ownership claim.
 * 
 * VIVA TIP:
 * The handoverOtp field is protected - it is ONLY populated when the logged-in user
 * is the actual claimant who owns this claim. Admins cannot see the code in advance,
 * ensuring high security at pickup!
 */
public class ClaimResponse {

    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemCategory;
    private String itemLocation;
    private String itemImageUrl;
    private UserSummaryDto claimant;
    private String proofDescription;
    private String proofImageUrl;
    private ClaimStatus status;
    private String rejectionReason;
    private String handoverOtp; // Secure OTP visible only to claimant
    private LocalDateTime otpVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimResponse() {
    }

    public ClaimResponse(Claim claim, boolean includeOtp) {
        if (claim != null) {
            this.id = claim.getId();
            if (claim.getItem() != null) {
                this.itemId = claim.getItem().getId();
                this.itemTitle = claim.getItem().getTitle();
                this.itemCategory = claim.getItem().getCategory();
                this.itemLocation = claim.getItem().getLocation();
                this.itemImageUrl = claim.getItem().getImageUrl();
            }
            this.claimant = UserSummaryDto.fromEntity(claim.getClaimant());
            this.proofDescription = claim.getProofDescription();
            this.proofImageUrl = claim.getProofImageUrl();
            this.status = claim.getStatus();
            this.rejectionReason = claim.getRejectionReason();
            this.handoverOtp = includeOtp ? claim.getHandoverOtp() : null;
            this.otpVerifiedAt = claim.getOtpVerifiedAt();
            this.createdAt = claim.getCreatedAt();
            this.updatedAt = claim.getUpdatedAt();
        }
    }

    public static ClaimResponse fromEntity(Claim claim, boolean includeOtp) {
        return new ClaimResponse(claim, includeOtp);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public UserSummaryDto getClaimant() {
        return claimant;
    }

    public void setClaimant(UserSummaryDto claimant) {
        this.claimant = claimant;
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

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getHandoverOtp() {
        return handoverOtp;
    }

    public void setHandoverOtp(String handoverOtp) {
        this.handoverOtp = handoverOtp;
    }

    public LocalDateTime getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
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
