package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.ClaimRequest;
import com.campus.lostfound.dto.request.ClaimStatusUpdateRequest;
import com.campus.lostfound.dto.response.ClaimResponse;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.ClaimStatus;
import com.campus.lostfound.enums.ItemStatus;
import com.campus.lostfound.enums.Role;
import com.campus.lostfound.exception.BadRequestException;
import com.campus.lostfound.exception.ResourceNotFoundException;
import com.campus.lostfound.exception.UnauthorizedException;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ClaimService.
 * 
 * VIVA EXPLANATION:
 * Implements the 2-step verification protocol:
 * 1. Admin verifies proof details -> Generates cryptographically secure 6-digit OTP visible ONLY to claimant.
 * 2. Physical Handover -> Claimant arrives at desk and provides OTP; Admin validates OTP in system.
 *    Only then is the item state permanently updated to RECOVERED!
 */
@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            ItemRepository itemRepository,
                            FileStorageService fileStorageService,
                            NotificationService notificationService) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    @Override
    public ClaimResponse submitClaim(ClaimRequest request, MultipartFile proofImage, User claimant) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        if (item.getStatus() == ItemStatus.RECOVERED) {
            throw new BadRequestException("This item has already been recovered and is no longer open for claims.");
        }

        if (item.getReporter().getId().equals(claimant.getId())) {
            throw new BadRequestException("You cannot submit a claim on an item that you reported yourself.");
        }

        if (claimRepository.existsByItemIdAndClaimantId(item.getId(), claimant.getId())) {
            throw new BadRequestException("You have already submitted a claim for this item.");
        }

        String proofImageUrl = null;
        if (proofImage != null && !proofImage.isEmpty()) {
            proofImageUrl = fileStorageService.storeFile(proofImage);
        } else if (request.getProofImageUrl() != null && !request.getProofImageUrl().isBlank()) {
            proofImageUrl = request.getProofImageUrl();
        }

        Claim claim = new Claim(item, claimant, request.getProofDescription().trim(), proofImageUrl);
        Claim savedClaim = claimRepository.save(claim);

        // Notify claimant
        notificationService.sendNotification(
                claimant,
                "Claim Submitted",
                "Your ownership claim for '" + item.getTitle() + "' has been submitted and is pending admin verification.",
                "CLAIM_UPDATE",
                savedClaim.getId()
        );

        return ClaimResponse.fromEntity(savedClaim, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(claim -> ClaimResponse.fromEntity(claim, false)) // OTP hidden from general list
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(User claimant) {
        return claimRepository.findByClaimantIdOrderByCreatedAtDesc(claimant.getId())
                .stream()
                .map(claim -> ClaimResponse.fromEntity(claim, true)) // OTP VISIBLE to claimant
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByItemId(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isReporter = item.getReporter().getId().equals(user.getId());

        if (!isAdmin && !isReporter) {
            throw new UnauthorizedException("You are not authorized to view claims for this item.");
        }

        return claimRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                .stream()
                .map(claim -> ClaimResponse.fromEntity(claim, false))
                .toList();
    }

    @Override
    public ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request, User admin) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (request.getStatus() == ClaimStatus.VERIFIED) {
            // Generate 6-digit random handover OTP
            int randomCode = secureRandom.nextInt(900000) + 100000; // 100000 to 999999
            String handoverOtp = String.valueOf(randomCode);

            claim.setStatus(ClaimStatus.VERIFIED);
            claim.setHandoverOtp(handoverOtp);
            claim.setRejectionReason(null);

            // Update item to CLAIM_PENDING
            Item item = claim.getItem();
            item.setStatus(ItemStatus.CLAIM_PENDING);
            itemRepository.save(item);

            // Notify claimant with OTP retrieval alert
            notificationService.sendNotification(
                    claim.getClaimant(),
                    "Claim Approved! Collect Your Item",
                    "Your claim for '" + item.getTitle() + "' was approved! Your 6-digit Pickup OTP is available in your 'My Claims' dashboard. Present this at the campus desk.",
                    "CLAIM_UPDATE",
                    claim.getId()
            );

        } else if (request.getStatus() == ClaimStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BadRequestException("A rejection reason is mandatory when rejecting a claim.");
            }

            claim.setStatus(ClaimStatus.REJECTED);
            claim.setRejectionReason(request.getRejectionReason().trim());
            claim.setHandoverOtp(null);

            // If the item was awaiting pickup for this claim, revert to OPEN
            Item item = claim.getItem();
            if (item.getStatus() == ItemStatus.CLAIM_PENDING) {
                item.setStatus(ItemStatus.OPEN);
                itemRepository.save(item);
            }

            // Notify claimant of rejection
            notificationService.sendNotification(
                    claim.getClaimant(),
                    "Claim Update: Rejected",
                    "Your claim for '" + item.getTitle() + "' was not approved. Reason: " + request.getRejectionReason(),
                    "CLAIM_UPDATE",
                    claim.getId()
            );
        } else {
            throw new BadRequestException("Invalid claim status update request. Expected VERIFIED or REJECTED.");
        }

        Claim updated = claimRepository.save(claim);
        return ClaimResponse.fromEntity(updated, false);
    }

    @Override
    public ClaimResponse verifyHandoverOtp(Long claimId, String otp, User admin) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (claim.getStatus() != ClaimStatus.VERIFIED) {
            throw new BadRequestException("This claim is not in VERIFIED status. Current status: " + claim.getStatus());
        }

        if (claim.getHandoverOtp() == null || !claim.getHandoverOtp().equals(otp.trim())) {
            throw new BadRequestException("Incorrect OTP. Please check the code shown on the claimant's dashboard.");
        }

        // OTP is correct! Complete handover
        claim.setStatus(ClaimStatus.RECOVERED);
        claim.setOtpVerifiedAt(LocalDateTime.now());

        Item item = claim.getItem();
        item.setStatus(ItemStatus.RECOVERED);
        itemRepository.save(item);

        Claim savedClaim = claimRepository.save(claim);

        // Notify claimant
        notificationService.sendNotification(
                claim.getClaimant(),
                "Item Successfully Recovered!",
                "Handover verified. You have successfully received '" + item.getTitle() + "'.",
                "ITEM_STATUS",
                item.getId()
        );

        // Notify reporter if different
        if (!item.getReporter().getId().equals(claim.getClaimant().getId())) {
            notificationService.sendNotification(
                    item.getReporter(),
                    "Item Recovered!",
                    "The item '" + item.getTitle() + "' you reported has been claimed and recovered by its owner.",
                    "ITEM_STATUS",
                    item.getId()
            );
        }

        return ClaimResponse.fromEntity(savedClaim, false);
    }
}
