package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.ClaimRequest;
import com.campus.lostfound.dto.request.ClaimStatusUpdateRequest;
import com.campus.lostfound.dto.response.ClaimResponse;
import com.campus.lostfound.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for claims, admin verification, and secure OTP handover.
 */
public interface ClaimService {

    ClaimResponse submitClaim(ClaimRequest request, MultipartFile proofImage, User claimant);

    List<ClaimResponse> getAllClaims();

    List<ClaimResponse> getMyClaims(User claimant);

    List<ClaimResponse> getClaimsByItemId(Long itemId, User user);

    ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request, User admin);

    ClaimResponse verifyHandoverOtp(Long claimId, String otp, User admin);
}
