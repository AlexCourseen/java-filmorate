package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String ADD_LIKE = """
        INSERT INTO review_likes (review_id, user_id, is_like)
        VALUES (?, ?, true)
    """;

    private static final String ADD_DISLIKE = """
        INSERT INTO review_likes (review_id, user_id, is_like)
        VALUES (?, ?, false)
    """;

    private static final String REMOVE_LIKE = """
        DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true
    """;

    private static final String REMOVE_DISLIKE = """
        DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false
    """;

    private static final String EXISTS_LIKE = """
        SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = true
    """;

    private static final String EXISTS_DISLIKE = """
        SELECT COUNT(*) FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = false
    """;

    private static final String REMOVE_ALL_BY_REVIEW_ID = """
        DELETE FROM review_likes WHERE review_id = ?
    """;

    @Override
    public void addLike(Long reviewId, Long userId) {
        jdbcTemplate.update(ADD_LIKE, reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        jdbcTemplate.update(ADD_DISLIKE, reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        jdbcTemplate.update(REMOVE_LIKE, reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        jdbcTemplate.update(REMOVE_DISLIKE, reviewId, userId);
    }

    @Override
    public boolean existsLike(Long reviewId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_LIKE, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsDislike(Long reviewId, Long userId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_DISLIKE, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public void removeAllByReviewId(Long reviewId) {
        jdbcTemplate.update(REMOVE_ALL_BY_REVIEW_ID, reviewId);
    }
}