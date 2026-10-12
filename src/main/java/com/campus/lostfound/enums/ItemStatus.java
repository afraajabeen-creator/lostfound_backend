package com.campus.lostfound.enums;

/**
 * Tracks the lifecycle status of an item:
 * OPEN: Active listing, available for claims or matching.
 * CLAIM_PENDING: A claim has been verified by admin, awaiting physical OTP pickup.
 * RECOVERED: The item has been physically collected and confirmed via OTP.
 */
public enum ItemStatus {
    OPEN,
    CLAIM_PENDING,
    RECOVERED
}
