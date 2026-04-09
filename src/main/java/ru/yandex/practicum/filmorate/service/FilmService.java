package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
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

    public Film setLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        if (userStorage.getUser(userId) != null) {
            checkLikes(film);
            film.getLikes().add(userId);
        }
        return film;
    }

    public Film delLike(long filmId, long userId) {
        Film film = getFilm(filmId);
        if (userStorage.getUser(userId) != null) {
            checkLikes(film);
            film.getLikes().remove(userId);
        }
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> {
                    checkLikes(film);
                    return film.getLikes().size();
                }).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkLikes(Film film) {
        if (film.getLikes() == null) {
            Set<Long> likes = new HashSet<>();
            film.setLikes(likes);
        }
    }
}
