package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String CREATE_REVIEW = """
        INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
        VALUES (?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_REVIEW = """
        UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?
    """;

    private static final String DELETE_REVIEW = """
        DELETE FROM reviews WHERE review_id = ?
    """;

    private static final String GET_REVIEW_BY_ID = """
        SELECT * FROM reviews WHERE review_id = ?
    """;

    private static final String GET_REVIEWS_BY_FILM_ID = """
        SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?
    """;

    private static final String GET_ALL_REVIEWS = """
        SELECT * FROM reviews ORDER BY useful DESC LIMIT ?
    """;

    private static final String UPDATE_USEFUL = """
        UPDATE reviews SET useful = ? WHERE review_id = ?
    """;

    private static final String EXISTS_BY_USER_AND_FILM = """
        SELECT COUNT(*) FROM reviews WHERE user_id = ? AND film_id = ?
    """;

    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review create(Review review) {
        long reviewId = insert(CREATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0
        );
        review.setReviewId(reviewId);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getById(review.getReviewId()).orElse(null);
    }

    @Override
    public void delete(Long reviewId) {
        update(DELETE_REVIEW, reviewId);
    }

    @Override
    public Optional<Review> getById(Long reviewId) {
        return findOne(GET_REVIEW_BY_ID, reviewId);
    }

    @Override
    public Collection<Review> getByFilmId(Long filmId, int limit) {
        return findMany(GET_REVIEWS_BY_FILM_ID, filmId, limit);
    }

    @Override
    public Collection<Review> getAll(int limit) {
        return findMany(GET_ALL_REVIEWS, limit);
    }

    @Override
    public void updateUseful(Long reviewId, Integer useful) {
        update(UPDATE_USEFUL, useful, reviewId);
    }

    @Override
    public boolean existsByUserAndFilm(Long userId, Long filmId) {
        Integer count = jdbc.queryForObject(EXISTS_BY_USER_AND_FILM, Integer.class, userId, filmId);
        return count != null && count > 0;
    }
}

