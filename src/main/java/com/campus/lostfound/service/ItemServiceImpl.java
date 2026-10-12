package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.ItemRequest;
import com.campus.lostfound.dto.response.ItemResponse;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.ItemStatus;
import com.campus.lostfound.enums.ItemType;
import com.campus.lostfound.enums.Role;
import com.campus.lostfound.exception.ResourceNotFoundException;
import com.campus.lostfound.exception.UnauthorizedException;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of ItemService.
 * Handles item creation, image upload, search filtering, and triggers Smart Match analysis.
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;
    private final SmartMatchService smartMatchService;
    private final NotificationService notificationService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           FileStorageService fileStorageService,
                           SmartMatchService smartMatchService,
                           NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
        this.smartMatchService = smartMatchService;
        this.notificationService = notificationService;
    }

    @Override
    public ItemResponse createItem(ItemRequest request, MultipartFile image, User reporter) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.storeFile(image);
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            imageUrl = request.getImageUrl();
        }

        Item item = new Item(
                reporter,
                request.getType(),
                request.getTitle().trim(),
                request.getCategory().trim(),
                request.getDescription().trim(),
                request.getLocation().trim(),
                request.getIncidentDate(),
                imageUrl
        );

        Item savedItem = itemRepository.save(item);

        // Run the Smart Match engine to find potential counterparts!
        smartMatchService.findAndSaveMatches(savedItem);

        // Send a confirmation notification to the reporter
        notificationService.sendNotification(
                reporter,
                "Item Reported Successfully",
                "Your " + savedItem.getType() + " report for '" + savedItem.getTitle() + "' has been published.",
                "ITEM_STATUS",
                savedItem.getId()
        );

        return ItemResponse.fromEntity(savedItem);
    }

    @Override
    public ItemResponse updateItem(Long id, ItemRequest request, MultipartFile image, User user) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        // Only reporter or admin can edit
        if (!item.getReporter().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to edit this item");
        }

        if (image != null && !image.isEmpty()) {
            String newImageUrl = fileStorageService.storeFile(image);
            if (item.getImageUrl() != null) {
                fileStorageService.deleteFile(item.getImageUrl());
            }
            item.setImageUrl(newImageUrl);
        } else if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }

        item.setType(request.getType());
        item.setTitle(request.getTitle().trim());
        item.setCategory(request.getCategory().trim());
        item.setDescription(request.getDescription().trim());
        item.setLocation(request.getLocation().trim());
        item.setIncidentDate(request.getIncidentDate());

        Item updated = itemRepository.save(item);

        // Re-run matching after edit
        smartMatchService.findAndSaveMatches(updated);

        return ItemResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return ItemResponse.fromEntity(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> searchItems(ItemType type, String category, String location,
                                         LocalDate startDate, LocalDate endDate, String keyword) {
        // Clean blank strings to null so SQL IS NULL check works seamlessly
        String cleanCat = (category != null && !category.isBlank()) ? category.trim() : null;
        String cleanLoc = (location != null && !location.isBlank()) ? location.trim() : null;
        String cleanKey = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return itemRepository.searchItems(type, cleanCat, cleanLoc, startDate, endDate, cleanKey)
                .stream()
                .map(ItemResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> getMyReportedItems(User user) {
        return itemRepository.findByReporterIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(ItemResponse::fromEntity)
                .toList();
    }

    @Override
    public void deleteItem(Long id, User user) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        if (!item.getReporter().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You are not authorized to delete this item");
        }

        if (item.getImageUrl() != null) {
            fileStorageService.deleteFile(item.getImageUrl());
        }

        itemRepository.delete(item);
    }
}
