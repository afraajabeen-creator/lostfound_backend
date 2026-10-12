package com.campus.lostfound.controller;

import com.campus.lostfound.dto.request.ClaimRequest;
import com.campus.lostfound.dto.request.ClaimStatusUpdateRequest;
import com.campus.lostfound.dto.request.OtpVerificationRequest;
import com.campus.lostfound.dto.response.ApiResponse;
import com.campus.lostfound.dto.response.ClaimResponse;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.AuthService;
import com.campus.lostfound.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for claims, admin approval/rejection, and handover OTP verification.
 */
@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;
    private final AuthService authService;

    public ClaimController(ClaimService claimService, AuthService authService) {
        this.claimService = claimService;
        this.authService = authService;
    }

    /**
     * Submit an ownership claim for an item.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClaimResponse>> submitClaim(
            @Valid @RequestBody ClaimRequest request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        ClaimResponse created = claimService.submitClaim(request, null, currentUser);
        return new ResponseEntity<>(ApiResponse.success("Claim submitted successfully", created), HttpStatus.CREATED);
    }

    /**
     * View claims submitted by the logged-in student (includes Handover OTP when status is VERIFIED).
     */
    @GetMapping("/my-claims")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getMyClaims(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        List<ClaimResponse> claims = claimService.getMyClaims(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Your claims retrieved", claims));
    }

    /**
     * View claims submitted for a specific item (for reporter or admin).
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getClaimsByItemId(
            @PathVariable Long itemId,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        List<ClaimResponse> claims = claimService.getClaimsByItemId(itemId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Claims retrieved for item", claims));
    }

    /**
     * Admin view of all claims across campus.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getAllClaims() {
        List<ClaimResponse> claims = claimService.getAllClaims();
        return ResponseEntity.ok(ApiResponse.success("All claims retrieved", claims));
    }

    /**
     * Admin approves (VERIFIED) or rejects (REJECTED) a claim.
     * Approving automatically generates a 6-digit Handover OTP for the student!
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> updateClaimStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateRequest request,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication.getName());
        ClaimResponse updated = claimService.updateClaimStatus(id, request, admin);
        return ResponseEntity.ok(ApiResponse.success("Claim status updated successfully", updated));
    }

    /**
     * Admin enters the student's 6-digit OTP at pickup desk to complete handover.
     */
    @PostMapping("/{id}/verify-otp")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> verifyHandoverOtp(
            @PathVariable Long id,
            @Valid @RequestBody OtpVerificationRequest request,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication.getName());
        ClaimResponse updated = claimService.verifyHandoverOtp(id, request.getOtp(), admin);
        return ResponseEntity.ok(ApiResponse.success("Handover confirmed! Item is now marked as Recovered.", updated));
    }
}
