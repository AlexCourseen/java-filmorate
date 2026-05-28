package ru.yandex.practicum.filmorate.storage.recomendation;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface RecommendationStorage {

    Collection<Film> getRecommendations(long userId);
}
