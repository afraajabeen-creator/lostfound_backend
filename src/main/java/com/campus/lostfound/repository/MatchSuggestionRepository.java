package com.campus.lostfound.repository;

import com.campus.lostfound.entity.MatchSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MatchSuggestion entity.
 */
@Repository
public interface MatchSuggestionRepository extends JpaRepository<MatchSuggestion, Long> {

    List<MatchSuggestion> findByLostItemIdOrFoundItemIdOrderByScoreDesc(Long lostItemId, Long foundItemId);

    List<MatchSuggestion> findByLostItemIdOrderByScoreDesc(Long lostItemId);

    List<MatchSuggestion> findByFoundItemIdOrderByScoreDesc(Long foundItemId);

    boolean existsByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);
}
