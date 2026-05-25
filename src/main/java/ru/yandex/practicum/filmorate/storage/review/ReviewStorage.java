package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(Long reviewId);

    Optional<Review> getById(Long reviewId);

    Collection<Review> getByFilmId(Long filmId, int limit);

    Collection<Review> getAll(int limit);

    void updateUseful(Long reviewId, Integer useful);

    boolean existsByUserAndFilm(Long userId, Long filmId);
}
