package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {
    LocalDate START_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    Collection<Film> getAllFilms();

    Film getFilm(long id);

    Film createFilm(Film newFilm);

    Film updateFilm(Film newFilm);

    Collection<Film> getPopularFilms(int count, Integer genreId, Integer year);

    Collection<Film> getFilmsByDirector(long id, String query);
}
