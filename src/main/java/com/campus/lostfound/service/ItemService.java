package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.ItemRequest;
import com.campus.lostfound.dto.response.ItemResponse;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.ItemType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for item management and listing discovery.
 */
public interface ItemService {

    ItemResponse createItem(ItemRequest request, MultipartFile image, User reporter);

    ItemResponse updateItem(Long id, ItemRequest request, MultipartFile image, User user);

    ItemResponse getItemById(Long id);

    List<ItemResponse> searchItems(ItemType type, String category, String location,
                                   LocalDate startDate, LocalDate endDate, String keyword);

    List<ItemResponse> getMyReportedItems(User user);

    void deleteItem(Long id, User user);
}
