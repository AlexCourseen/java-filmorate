package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeStorage {
    void addLike(long filmId, long userId);
    void delLike(long filmId, long userId);
    Collection<Like> getLikes(long filmId);
}