package com.campus.lostfound.service;

import com.campus.lostfound.dto.response.MatchSuggestionResponse;
import com.campus.lostfound.entity.Item;
import com.campus.lostfound.entity.MatchSuggestion;
import com.campus.lostfound.enums.ItemStatus;
import com.campus.lostfound.enums.ItemType;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.MatchSuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Smart Match Engine Implementation.
 * 
 * VIVA EXPLANATION:
 * This algorithm implements a multi-factor similarity score between LOST and FOUND items
 * using pure Java (no heavy external machine learning library needed):
 * 1. Category Match (Weight: 35%) -> Exact match of item category
 * 2. Location Proximity (Weight: 25%) -> Token overlap of campus buildings/rooms
 * 3. Date Proximity (Weight: 20%) -> Time delta in days between loss and recovery
 * 4. Keyword Similarity (Weight: 20%) -> Jaccard Similarity Coefficient over description tokens
 * Total Score = Sum of 4 weighted scores (0 to 100)
 * Items with Score >= 50% are recorded as Possible Matches and trigger notifications.
 */
@Service
@Transactional
public class SmartMatchServiceImpl implements SmartMatchService {

    private static final double MATCH_THRESHOLD = 50.0;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further",
            "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him", "himself",
            "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me", "more",
            "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once", "only", "or",
            "other", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so",
            "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then",
            "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why",
            "with", "would", "you", "your", "yours", "yourself", "yourselves"
    );

    private final ItemRepository itemRepository;
    private final MatchSuggestionRepository matchSuggestionRepository;
    private final NotificationService notificationService;

    public SmartMatchServiceImpl(ItemRepository itemRepository,
                                MatchSuggestionRepository matchSuggestionRepository,
                                NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.matchSuggestionRepository = matchSuggestionRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void findAndSaveMatches(Item item) {
        if (item.getStatus() != ItemStatus.OPEN) {
            return;
        }

        // Compare against items of the opposite type that are OPEN
        ItemType oppositeType = (item.getType() == ItemType.LOST) ? ItemType.FOUND : ItemType.LOST;
        List<Item> candidates = itemRepository.findByTypeAndStatus(oppositeType, ItemStatus.OPEN);

        for (Item candidate : candidates) {
            // Cannot match item with itself or items from same user if desired
            if (candidate.getId().equals(item.getId())) {
                continue;
            }

            Item lostItem = (item.getType() == ItemType.LOST) ? item : candidate;
            Item foundItem = (item.getType() == ItemType.FOUND) ? item : candidate;

            // Check if already matched
            if (matchSuggestionRepository.existsByLostItemIdAndFoundItemId(lostItem.getId(), foundItem.getId())) {
                continue;
            }

            // Calculate similarity score
            ScoreResult result = calculateSimilarity(lostItem, foundItem);

            if (result.score >= MATCH_THRESHOLD) {
                MatchSuggestion suggestion = new MatchSuggestion(
                        lostItem,
                        foundItem,
                        Math.round(result.score * 10.0) / 10.0,
                        result.reasons
                );
                matchSuggestionRepository.save(suggestion);

                // Notify both reporters
                notificationService.sendNotification(
                        lostItem.getReporter(),
                        "Possible Match Found!",
                        "A found item matching your reported '" + lostItem.getTitle() + "' was discovered: '" +
                                foundItem.getTitle() + "' (Match Score: " + Math.round(result.score) + "%).",
                        "MATCH_FOUND",
                        lostItem.getId()
                );

                notificationService.sendNotification(
                        foundItem.getReporter(),
                        "Possible Match Found!",
                        "Your found item '" + foundItem.getTitle() + "' matches a reported lost item: '" +
                                lostItem.getTitle() + "' (Match Score: " + Math.round(result.score) + "%).",
                        "MATCH_FOUND",
                        foundItem.getId()
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchSuggestionResponse> getMatchesForItem(Long itemId) {
        return matchSuggestionRepository.findByLostItemIdOrFoundItemIdOrderByScoreDesc(itemId, itemId)
                .stream()
                .map(MatchSuggestionResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchSuggestionResponse> getMatchesForUser(Long userId) {
        List<Item> userItems = itemRepository.findByReporterIdOrderByCreatedAtDesc(userId);
        List<MatchSuggestionResponse> results = new ArrayList<>();

        for (Item userItem : userItems) {
            List<MatchSuggestion> suggestions = matchSuggestionRepository
                    .findByLostItemIdOrFoundItemIdOrderByScoreDesc(userItem.getId(), userItem.getId());
            for (MatchSuggestion s : suggestions) {
                results.add(MatchSuggestionResponse.fromEntity(s));
            }
        }

        // Sort descending by score
        results.sort(Comparator.comparingDouble(MatchSuggestionResponse::getScore).reversed());
        return results;
    }

    /**
     * Calculates the composite similarity score between a lost and found item.
     */
    private ScoreResult calculateSimilarity(Item lost, Item found) {
        double categoryScore = 0.0;
        double locationScore = 0.0;
        double dateScore = 0.0;
        double keywordScore = 0.0;
        List<String> reasonParts = new ArrayList<>();

        // 1. Category Match (Weight: 35%)
        if (lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            categoryScore = 35.0;
            reasonParts.add("Category Match (" + lost.getCategory() + "): +35%");
        }

        // 2. Location Match (Weight: 25%)
        if (lost.getLocation().equalsIgnoreCase(found.getLocation())) {
            locationScore = 25.0;
            reasonParts.add("Exact Location Match: +25%");
        } else {
            double locOverlap = calculateTokenOverlap(lost.getLocation(), found.getLocation());
            if (locOverlap > 0.0) {
                locationScore = Math.min(25.0, locOverlap * 25.0);
                reasonParts.add("Location Proximity: +" + Math.round(locationScore) + "%");
            }
        }

        // 3. Date Proximity (Weight: 20%)
        long daysDiff = Math.abs(ChronoUnit.DAYS.between(lost.getIncidentDate(), found.getIncidentDate()));
        if (daysDiff <= 1) {
            dateScore = 20.0;
            reasonParts.add("Date Proximity (" + daysDiff + " day delta): +20%");
        } else if (daysDiff <= 3) {
            dateScore = 15.0;
            reasonParts.add("Date Proximity (" + daysDiff + " days delta): +15%");
        } else if (daysDiff <= 7) {
            dateScore = 10.0;
            reasonParts.add("Date Proximity (" + daysDiff + " days delta): +10%");
        } else if (daysDiff <= 14) {
            dateScore = 5.0;
            reasonParts.add("Date Proximity (" + daysDiff + " days delta): +5%");
        }

        // 4. Keyword Similarity using Jaccard Similarity (Weight: 20%)
        String lostText = lost.getTitle() + " " + lost.getDescription();
        String foundText = found.getTitle() + " " + found.getDescription();
        double jaccard = calculateJaccardSimilarity(lostText, foundText);
        if (jaccard > 0.0) {
            keywordScore = Math.min(20.0, jaccard * 20.0 * 2.0); // Boost coefficient
            reasonParts.add("Description & Title Keyword Match: +" + Math.round(keywordScore) + "%");
        }

        double totalScore = categoryScore + locationScore + dateScore + keywordScore;
        String reasons = String.join(", ", reasonParts);

        return new ScoreResult(totalScore, reasons);
    }

    /**
     * Computes Jaccard Similarity = |A ∩ B| / |A ∪ B| over cleaned word sets.
     */
    private double calculateJaccardSimilarity(String textA, String textB) {
        Set<String> wordsA = extractMeaningfulWords(textA);
        Set<String> wordsB = extractMeaningfulWords(textB);

        if (wordsA.isEmpty() || wordsB.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);

        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);

        return (double) intersection.size() / union.size();
    }

    /**
     * Overlap ratio for multi-word locations (e.g. "Library 2nd Floor" vs "Library").
     */
    private double calculateTokenOverlap(String locA, String locB) {
        Set<String> wordsA = extractMeaningfulWords(locA);
        Set<String> wordsB = extractMeaningfulWords(locB);

        if (wordsA.isEmpty() || wordsB.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);

        int minSize = Math.min(wordsA.size(), wordsB.size());
        return (double) intersection.size() / minSize;
    }

    private Set<String> extractMeaningfulWords(String text) {
        if (text == null) {
            return Collections.emptySet();
        }
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = cleaned.split("\\s+");
        Set<String> words = new HashSet<>();
        for (String token : tokens) {
            if (token.length() > 2 && !STOP_WORDS.contains(token)) {
                words.add(token);
            }
        }
        return words;
    }

    private static class ScoreResult {
        final double score;
        final String reasons;

        ScoreResult(double score, String reasons) {
            this.score = score;
            this.reasons = reasons;
        }
    }
}
