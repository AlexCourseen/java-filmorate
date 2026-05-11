package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    private static final String FIND_ALL_FILMS =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f LEFT JOIN rating r ON f.rating_id = r.rating_id";
    private static final String FIND_FILM_BY_ID =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f LEFT JOIN rating r ON f.rating_id = r.rating_id " +
                    "WHERE f.film_id = ?";
    private static final String CREATE_FILM =
            "INSERT INTO film(name, description, releaseDate, duration, rating_id)" +
                    " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE film SET name=?, description=?, releaseDate=?," +
            " duration=?, rating_id=? WHERE film_id = ?";
    private static final String GENRES_TO_FILM = "INSERT INTO film_genre(genre_id, film_id) VALUES (?, ?)";
    private static final String GET_POPULAR_FILMS = "SELECT f.*, r.NAME as mpa_name " +
            "FROM film f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
            "GROUP BY f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    private final static String DEL_FILM = "DELETE FROM users WHERE user_id = ?";


    @Autowired
    FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreStorage, LikeStorage likeStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = findMany(FIND_ALL_FILMS);
        films.forEach(this::setLikesAndGenres);
        return films;
    }

    @Override
    public Film getFilm(long id) {
        Film film = findOne(FIND_FILM_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
        setLikesAndGenres(film);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        Object[] params = new Object[]{
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        };
        long filmId = insert(CREATE_FILM, params);
        saveGenresToFilm(film, filmId);
        return getFilm(filmId);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        long filmId = newFilm.getId();
        if (getFilm(filmId) != null) {
            Object[] params = new Object[]{
                    newFilm.getName(),
                    newFilm.getDescription(),
                    newFilm.getReleaseDate(),
                    newFilm.getDuration(),
                    newFilm.getMpa().getId(),
                    filmId
            };
            update(UPDATE_FILM, params);
            return getFilm(filmId);
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> films = findMany(GET_POPULAR_FILMS, count);
        films.forEach(this::setLikesAndGenres);
        return films;
    }

    public void delFim(long id) {
        update(DEL_FILM,id);
    }

    private void checkFilm(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть более 200 символов");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность не может меньше 0");
        }
        if (film.getReleaseDate().isBefore(START_FILM_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше " + START_FILM_RELEASE_DATE);
        }
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 1) {
            throw new NotFoundException("ID рейтинга должен быть от 1 до 5 включительно");
        }

        Set<Long> genresId = genreStorage.getAllGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        boolean isCorrectGenres = film.getGenres()
                .stream()
                .map(Genre::getId)
                .allMatch(genresId::contains);
        if (!isCorrectGenres) {
            throw new NotFoundException("Жанры не найдены");
        }
    }

    private void saveGenresToFilm(Film film, long filmId) {
        Set<Genre> filmGenre = film.getGenres();
        if (!filmGenre.isEmpty()) {
            filmGenre.forEach(genre -> update(GENRES_TO_FILM, genre.getId(), filmId));
        }
    }

    private void setLikesAndGenres(Film film) {
        film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId())));
        film.setLikes(new HashSet<>(likeStorage.getLikes(film.getId())));
    }
}
