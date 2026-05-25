package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    private static final String CREATE_REVIEW = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_REVIEW = """
            UPDATE reviews SET content = ?, is_positive = ?
            WHERE review_id = ?
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

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_REVIEW, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, 0); // при создании рейтинг равен 0
            return ps;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        return getById(review.getReviewId()).orElse(null);
    }

    @Override
    public void delete(Long reviewId) {
        jdbcTemplate.update(DELETE_REVIEW, reviewId);
    }

    @Override
    public Optional<Review> getById(Long reviewId) {
        List<Review> result = jdbcTemplate.query(GET_REVIEW_BY_ID, reviewRowMapper, reviewId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Review> getByFilmId(Long filmId, int limit) {
        return jdbcTemplate.query(GET_REVIEWS_BY_FILM_ID, reviewRowMapper, filmId, limit);
    }

    @Override
    public List<Review> getAll(int limit) {
        return jdbcTemplate.query(GET_ALL_REVIEWS, reviewRowMapper, limit);
    }

    @Override
    public void updateUseful(Long reviewId, Integer useful) {
        jdbcTemplate.update(UPDATE_USEFUL, useful, reviewId);
    }

    @Override
    public boolean existsByUserAndFilm(Long userId, Long filmId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_USER_AND_FILM, Integer.class, userId, filmId);
        return count != null && count > 0;
    }
}

