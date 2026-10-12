package com.campus.lostfound.service;

import com.campus.lostfound.dto.response.MatchSuggestionResponse;
import com.campus.lostfound.entity.Item;

import java.util.List;

/**
 * Service interface for the automated Smart Match Engine.
 */
public interface SmartMatchService {

    void findAndSaveMatches(Item item);

    List<MatchSuggestionResponse> getMatchesForItem(Long itemId);

    List<MatchSuggestionResponse> getMatchesForUser(Long userId);
}
