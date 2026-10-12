package com.campus.lostfound.controller;

import com.campus.lostfound.dto.request.ItemRequest;
import com.campus.lostfound.dto.response.ApiResponse;
import com.campus.lostfound.dto.response.ItemResponse;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.ItemType;
import com.campus.lostfound.service.AuthService;
import com.campus.lostfound.service.FileStorageService;
import com.campus.lostfound.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing Lost and Found item listings.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final AuthService authService;
    private final FileStorageService fileStorageService;

    public ItemController(ItemService itemService,
                          AuthService authService,
                          FileStorageService fileStorageService) {
        this.itemService = itemService;
        this.authService = authService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Upload an item image directly and get its accessible URL.
     */
    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", Map.of("imageUrl", fileUrl)));
    }

    /**
     * Create item via JSON body.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ItemResponse>> createItemJson(
            @Valid @RequestBody ItemRequest request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        ItemResponse created = itemService.createItem(request, null, currentUser);
        return new ResponseEntity<>(ApiResponse.success("Item reported successfully", created), HttpStatus.CREATED);
    }

    /**
     * Create item via multipart form-data (optional image attached in same request).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemResponse>> createItemMultipart(
            @RequestParam("type") ItemType type,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("incidentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate incidentDate,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        ItemRequest request = new ItemRequest(type, title, category, description, location, incidentDate, null);
        ItemResponse created = itemService.createItem(request, image, currentUser);
        return new ResponseEntity<>(ApiResponse.success("Item reported successfully", created), HttpStatus.CREATED);
    }

    /**
     * Search and filter items with optional criteria.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemResponse>>> searchItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword) {
        List<ItemResponse> items = itemService.searchItems(type, category, location, startDate, endDate, keyword);
        return ResponseEntity.ok(ApiResponse.success("Items retrieved successfully", items));
    }

    /**
     * Retrieve a specific item by its primary key ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Item details retrieved", item));
    }

    /**
     * Get items reported by current logged-in user.
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> getMyItems(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        List<ItemResponse> items = itemService.getMyReportedItems(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Your reported items retrieved", items));
    }

    /**
     * Update an item.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        ItemResponse updated = itemService.updateItem(id, request, null, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", updated));
    }

    /**
     * Delete an item.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        itemService.deleteItem(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully"));
    }
}
