package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("POST /reviews - создание отзыва");
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("PUT /reviews - обновление отзыва с id={}", review.getReviewId());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long reviewId) {
        log.info("DELETE /reviews/{} - удаление отзыва", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") Long reviewId) {
        log.info("GET /reviews/{} - получение отзыва", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("GET /reviews - получение отзывов, filmId={}, count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/like/{} - добавление лайка отзыву", reviewId, userId);
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        log.info("PUT /reviews/{}/dislike/{} - добавление дизлайка отзыву", reviewId, userId);
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/like/{} - удаление лайка с отзыва", reviewId, userId);
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        log.info("DELETE /reviews/{}/dislike/{} - удаление дизлайка с отзыва", reviewId, userId);
        reviewService.removeDislike(reviewId, userId);
    }
}
