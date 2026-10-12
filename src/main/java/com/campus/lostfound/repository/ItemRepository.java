package com.campus.lostfound.repository;

import com.campus.lostfound.entity.Item;
import com.campus.lostfound.enums.ItemStatus;
import com.campus.lostfound.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Item entity.
 * Provides custom queries for searching, filtering, and reporting items.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    List<Item> findByTypeAndStatus(ItemType type, ItemStatus status);

    List<Item> findAllByOrderByCreatedAtDesc();

    /**
     * Search and filter items with optional parameters.
     * VIVA TIP:
     * Using JPQL with :param IS NULL OR field = :param allows dynamic multi-criteria
     * filtering (type, category, location, date range, keyword) in a single clean query!
     */
    @Query("SELECT i FROM Item i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:category IS NULL OR LOWER(i.category) = LOWER(:category)) AND " +
           "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:startDate IS NULL OR i.incidentDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.incidentDate <= :endDate) AND " +
           "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY i.createdAt DESC")
    List<Item> searchItems(
            @Param("type") ItemType type,
            @Param("category") String category,
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );
}
