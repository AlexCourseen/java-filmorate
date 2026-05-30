package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final EventStorage eventStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    public Review createReview(Review review) {
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());

        if (reviewStorage.existsByUserAndFilm(review.getUserId(), review.getFilmId())) {
            throw new ValidationException("Пользователь уже оставил отзыв на этот фильм");
        }

        Review createdReview = reviewStorage.create(review);
        log.info("Создан отзыв с id={} для фильма {} пользователем {}",
                createdReview.getReviewId(), createdReview.getFilmId(), createdReview.getUserId());
        eventStorage.addEvent(createdReview.getUserId(), EventType.REVIEW, Operation.ADD, createdReview.getReviewId());
        return createdReview;
    }

    public Review updateReview(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("ID отзыва обязателен для обновления");
        }

        Review existing = getReviewById(review.getReviewId());
        existing.setContent(review.getContent());
        existing.setIsPositive(review.getIsPositive());

        Review updatedReview = reviewStorage.update(existing);
        log.info("Обновлён отзыв с id={}", updatedReview.getReviewId());
        eventStorage.addEvent(existing.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        
        return updatedReview;
    }

    public void deleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        reviewLikeStorage.removeAllByReviewId(reviewId);
        reviewStorage.delete(reviewId);
        log.info("Удалён отзыв с id={}", reviewId);
        eventStorage.addEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId());
    }

    public Review getReviewById(Long reviewId) {
        return reviewStorage.getById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не найден"));
    }

    public Collection<Review> getReviews(Long filmId, Integer count) {
        if (count <= 0) {
            throw new ValidationException("Число отзывов должно быть положительным");
        }
        if (filmId != null) {
            filmStorage.getFilm(filmId);
            return reviewStorage.getByFilmId(filmId, count);
        } else {
            return reviewStorage.getAll(count);
        }
    }

    public void addDislike(Long reviewId, Long userId) {
        userStorage.getUser(userId);
        Review review = getReviewById(reviewId);

        if (reviewLikeStorage.existsLike(reviewId, userId)) {
            reviewLikeStorage.removeLike(reviewId, userId);
            reviewLikeStorage.addDislike(reviewId, userId);
            int newUseful = review.getUseful() - 2;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} сменил лайк на дизлайк для отзыва {}", userId, reviewId);
        } else if (!reviewLikeStorage.existsDislike(reviewId, userId)) {
            reviewLikeStorage.addDislike(reviewId, userId);
            int newUseful = review.getUseful() - 1;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
        }
    }

    public void addLike(Long reviewId, Long userId) {
        userStorage.getUser(userId);
        Review review = getReviewById(reviewId);

        if (reviewLikeStorage.existsDislike(reviewId, userId)) {
            reviewLikeStorage.removeDislike(reviewId, userId);
            reviewLikeStorage.addLike(reviewId, userId);
            int newUseful = review.getUseful() + 2;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} сменил дизлайк на лайк для отзыва {}", userId, reviewId);
        } else if (!reviewLikeStorage.existsLike(reviewId, userId)) {
            reviewLikeStorage.addLike(reviewId, userId);
            int newUseful = review.getUseful() + 1;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
        }
    }

    public void removeLike(Long reviewId, Long userId) {
        if (reviewLikeStorage.existsLike(reviewId, userId)) {
            reviewLikeStorage.removeLike(reviewId, userId);
            Review review = getReviewById(reviewId);
            int newUseful = review.getUseful() - 1;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} удалил лайк с отзыва {}", userId, reviewId);
        }
    }

    public void removeDislike(Long reviewId, Long userId) {
        if (reviewLikeStorage.existsDislike(reviewId, userId)) {
            reviewLikeStorage.removeDislike(reviewId, userId);
            Review review = getReviewById(reviewId);
            int newUseful = review.getUseful() + 1;
            reviewStorage.updateUseful(reviewId, newUseful);
            log.info("Пользователь {} удалил дизлайк с отзыва {}", userId, reviewId);
        }
    }
}