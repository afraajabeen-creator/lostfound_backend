package com.campus.lostfound.controller;

import com.campus.lostfound.dto.response.ApiResponse;
import com.campus.lostfound.dto.response.MatchSuggestionResponse;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.service.AuthService;
import com.campus.lostfound.service.SmartMatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for retrieving Smart Match suggestions between Lost and Found items.
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final SmartMatchService smartMatchService;
    private final AuthService authService;

    public MatchController(SmartMatchService smartMatchService, AuthService authService) {
        this.smartMatchService = smartMatchService;
        this.authService = authService;
    }

    /**
     * Get possible matches for a specific item.
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<List<MatchSuggestionResponse>>> getMatchesForItem(@PathVariable Long itemId) {
        List<MatchSuggestionResponse> matches = smartMatchService.getMatchesForItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Match suggestions retrieved", matches));
    }

    /**
     * Get all possible matches for items reported by current logged-in user.
     */
    @GetMapping("/my-matches")
    public ResponseEntity<ApiResponse<List<MatchSuggestionResponse>>> getMyMatches(Authentication authentication) {
        User currentUser = authService.getCurrentUser(authentication.getName());
        List<MatchSuggestionResponse> matches = smartMatchService.getMatchesForUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Your possible matches retrieved", matches));
    }
}
