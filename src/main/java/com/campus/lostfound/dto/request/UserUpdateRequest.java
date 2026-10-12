package com.campus.lostfound.dto.request;

import com.campus.lostfound.enums.Role;

/**
 * DTO for Admin updating user details or status.
 */
public class UserUpdateRequest {

    private String fullName;
    private String phoneNumber;
    private Role role;
    private Boolean isBlocked;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String fullName, String phoneNumber, Role role, Boolean isBlocked) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isBlocked = isBlocked;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
