package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewLikeRowMapper;

@Repository
public class ReviewLikeDbStorage extends BaseDbStorage<ReviewLike> implements ReviewLikeStorage {

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

    public ReviewLikeDbStorage(JdbcTemplate jdbc, ReviewLikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        update(ADD_LIKE, reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        update(ADD_DISLIKE, reviewId, userId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        update(REMOVE_LIKE, reviewId, userId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        update(REMOVE_DISLIKE, reviewId, userId);
    }

    @Override
    public boolean existsLike(Long reviewId, Long userId) {
        Integer count = jdbc.queryForObject(EXISTS_LIKE, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsDislike(Long reviewId, Long userId) {
        Integer count = jdbc.queryForObject(EXISTS_DISLIKE, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public void removeAllByReviewId(Long reviewId) {
        update(REMOVE_ALL_BY_REVIEW_ID, reviewId);
    }
}