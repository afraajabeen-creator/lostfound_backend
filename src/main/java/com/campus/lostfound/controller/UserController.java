package com.campus.lostfound.controller;

import com.campus.lostfound.dto.request.UserUpdateRequest;
import com.campus.lostfound.dto.response.ApiResponse;
import com.campus.lostfound.dto.response.UserSummaryDto;
import com.campus.lostfound.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Admin User Management.
 * 
 * VIVA TIP:
 * @PreAuthorize("hasAuthority('ROLE_ADMIN')") ensures that only users with the
 * ADMIN role can invoke these operations.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getAllUsers() {
        List<UserSummaryDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getUserById(@PathVariable Long id) {
        UserSummaryDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        UserSummaryDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
    }

    @PutMapping("/{id}/toggle-block")
    public ResponseEntity<ApiResponse<UserSummaryDto>> toggleBlockStatus(@PathVariable Long id) {
        UserSummaryDto updated = userService.toggleBlockStatus(id);
        String action = updated.isBlocked() ? "blocked" : "unblocked";
        return ResponseEntity.ok(ApiResponse.success("User has been " + action, updated));
    }
}
