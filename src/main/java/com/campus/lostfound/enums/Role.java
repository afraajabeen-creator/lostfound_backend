package com.campus.lostfound.enums;

/**
 * Defines user roles in the system.
 * ROLE_STUDENT: Can report items, search, submit claims, view matches, and receive OTP.
 * ROLE_ADMIN: Can manage users, verify claims, reject claims with reasons, and enter handover OTP.
 */
public enum Role {
    ROLE_STUDENT,
    ROLE_ADMIN
}
