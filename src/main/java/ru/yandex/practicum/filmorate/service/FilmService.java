package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final LikeStorage likeStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void setLike(long filmId, long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void delLike(long filmId, long userId) {
        likeStorage.delLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getFilmsByDirector(long id, String query) {
        return filmStorage.getFilmsByDirector(id, query);
    }

    public Collection<Film> searchFilms(String query, String searchBy) {
        Collection<Film> films;
        if ((!query.isBlank() || query != null) && (!searchBy.isBlank() || searchBy != null)) {
            films = filmStorage.searchFilms(query, searchBy);
        } else {
            int defaultValueCount = 10;
            films = filmStorage.getPopularFilms(defaultValueCount);
        }
        return films;
    }

    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getCommonFilms(long userId, long friendId) {
        userStorage.getUser(userId);
        userStorage.getUser(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
