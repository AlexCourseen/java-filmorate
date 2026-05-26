package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
public class Review {
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не должно быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва положительный/негативный обязателен")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID фильма обязателен")
    private Long filmId;

    private Integer useful; // рейтинг отзыва (полезный/бесполезный)
}
