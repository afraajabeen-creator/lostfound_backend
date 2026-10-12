package com.campus.lostfound.enums;

/**
 * Tracks the verification and handover status of a student's claim.
 * PENDING: Student submitted claim, awaiting admin review.
 * VERIFIED: Admin approved claim; 6-digit Handover OTP generated for the student.
 * REJECTED: Admin rejected the claim (with explanation/reason).
 * RECOVERED: Admin validated the 6-digit OTP at the desk; item handed over.
 */
public enum ClaimStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    RECOVERED
}
