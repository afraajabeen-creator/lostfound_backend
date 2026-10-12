package com.campus.lostfound.dto.response;

import com.campus.lostfound.entity.MatchSuggestion;
import java.time.LocalDateTime;

/**
 * DTO representing an automated smart match suggestion between a lost item and a found item.
 */
public class MatchSuggestionResponse {

    private Long id;
    private ItemResponse lostItem;
    private ItemResponse foundItem;
    private double score;
    private String matchReasons;
    private LocalDateTime createdAt;

    public MatchSuggestionResponse() {
    }

    public MatchSuggestionResponse(MatchSuggestion suggestion) {
        if (suggestion != null) {
            this.id = suggestion.getId();
            this.lostItem = ItemResponse.fromEntity(suggestion.getLostItem());
            this.foundItem = ItemResponse.fromEntity(suggestion.getFoundItem());
            this.score = suggestion.getScore();
            this.matchReasons = suggestion.getMatchReasons();
            this.createdAt = suggestion.getCreatedAt();
        }
    }

    public static MatchSuggestionResponse fromEntity(MatchSuggestion suggestion) {
        return new MatchSuggestionResponse(suggestion);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemResponse getLostItem() {
        return lostItem;
    }

    public void setLostItem(ItemResponse lostItem) {
        this.lostItem = lostItem;
    }

    public ItemResponse getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(ItemResponse foundItem) {
        this.foundItem = foundItem;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMatchReasons() {
        return matchReasons;
    }

    public void setMatchReasons(String matchReasons) {
        this.matchReasons = matchReasons;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
