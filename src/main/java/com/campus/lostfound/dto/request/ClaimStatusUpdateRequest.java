package com.campus.lostfound.dto.request;

import com.campus.lostfound.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for Admin approving or rejecting a claim.
 */
public class ClaimStatusUpdateRequest {

    @NotNull(message = "Status is required (VERIFIED or REJECTED)")
    private ClaimStatus status;

    private String rejectionReason;

    public ClaimStatusUpdateRequest() {
    }

    public ClaimStatusUpdateRequest(ClaimStatus status, String rejectionReason) {
        this.status = status;
        this.rejectionReason = rejectionReason;
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
}
