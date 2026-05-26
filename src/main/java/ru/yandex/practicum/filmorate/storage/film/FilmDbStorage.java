package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;

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
    private static final String DEL_FILM = "DELETE FROM users WHERE user_id = ?";
    private static final String GENRES_TO_FILM = "MERGE INTO film_genre(genre_id, film_id) " +
            "KEY(genre_id, film_id) VALUES (?, ?)";
    private static final String DEL_FILM_GENRES = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_POPULAR_FILMS_COMPLEX =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN rating r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "WHERE (? IS NULL OR fg.genre_id = ?) " +
                    "  AND (? IS NULL OR YEAR(f.releaseDate) = ?) " +
                    "GROUP BY f.film_id, r.name " +
                    "ORDER BY COUNT(DISTINCT l.user_id) DESC " +
                    "LIMIT ?";
    private static final String DIRECTORS_TO_FILM = "MERGE INTO film_director(director_id, film_id) " +
            "KEY(director_id, film_id) VALUES (?, ?)";
    private static final String FILMS_BY_DIR_ORDER_YEAR =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "JOIN film_director fd ON fd.film_id = f.film_id " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.releaseDate ASC";
    private static final String FILMS_BY_DIR_ORDER_LIKE =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "JOIN film_director fd ON fd.film_id=f.film_id " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "JOIN likes l ON l.film_id=f.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.film_id, f.name, f.description, f.releaseDate, f.duration, f.rating_id " +
                    "ORDER BY COUNT(l.user_id) DESC";
    private static final String DEL_FILM_DIRECTORS = "DELETE FROM film_director WHERE film_id = ?";
    private static final String SEARCH_FILMS_BY_DIRECTORS =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "JOIN film_director fd ON fd.film_id=f.film_id " +
                    "JOIN directors d ON d.director_id=fd.director_id " +
                    "WHERE d.name ILIKE ? ";
    private static final String SEARCH_FILMS_BY_TITLE =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "WHERE f.name ILIKE ? ";
    private static final String SEARCH_FILMS_BY_DIRS_AND_FILM_NAME =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM film f " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_director fd ON fd.film_id=f.film_id " +
                    "LEFT JOIN directors d ON d.director_id=fd.director_id " +
                    "WHERE d.name ILIKE ? OR f.name ILIKE ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreStorage genreStorage, LikeStorage likeStorage,
                         DirectorStorage directorStorage) {
        super(jdbc, mapper);
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = findMany(FIND_ALL_FILMS);
        films.forEach(f -> {
            setLikesAndGenres(f);
            setDirectors(f);
        });
        return films;
    }

    @Override
    public Film getFilm(long id) {
        Film film = findOne(FIND_FILM_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
        setLikesAndGenres(film);
        setDirectors(film);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        checkFilm(film);
        checkGenres(film);
        checkDirectors(film);
        Object[] params = new Object[]{
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        };
        long filmId = insert(CREATE_FILM, params);
        saveGenresToFilm(film, filmId);
        saveDirectorsToFilm(film, filmId);
        setDirectors(film);
        setLikesAndGenres(film);
        return getFilm(filmId);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        long filmId = newFilm.getId();
        if (getFilm(filmId) != null) {
            checkDirectors(newFilm);
            checkGenres(newFilm);
            Object[] params = new Object[]{
                    newFilm.getName(),
                    newFilm.getDescription(),
                    newFilm.getReleaseDate(),
                    newFilm.getDuration(),
                    newFilm.getMpa().getId(),
                    filmId
            };
            update(UPDATE_FILM, params);
            saveDirectorsToFilm(newFilm, filmId);
            saveGenresToFilm(newFilm, filmId);
            Film updatedFilm = getFilm(filmId);
            setDirectors(updatedFilm);
            setLikesAndGenres(updatedFilm);
            return updatedFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return getPopularFilms(count, null, null);
    }

    @Override
    public Collection<Film> getPopularFilms(int count, Integer genreId, String year) {
        Integer yearVal = (year != null) ? Integer.parseInt(year) : null;

        Collection<Film> films = findMany(GET_POPULAR_FILMS_COMPLEX,
                genreId, genreId,
                yearVal, yearVal,
                count);

        films.forEach(f -> {
            setLikesAndGenres(f);
            setDirectors(f);
        });
        return films;
    }

    @Override
    public Collection<Film> getFilmsByDirector(long id, String query) {
        Collection<Film> films = new ArrayList<>();
        if (directorStorage.getDirector(id) != null) {
            if (query.equals("likes")) {
                films = findMany(FILMS_BY_DIR_ORDER_LIKE, id);
            } else if (query.equals("year")) {
                films = findMany(FILMS_BY_DIR_ORDER_YEAR, id);
            }
        }
        films.forEach(f -> {
            setLikesAndGenres(f);
            setDirectors(f);
        });
        return films;
    }

    public void delFim(long id) {
        update(DEL_FILM, id);
    }

    public Collection<Film> searchFilms(String query, String searchBy) {
        Collection<Film> films = new ArrayList<>();
        String searchPattern = "%" + query + "%";
        if (searchBy.equals("director")) {
            films = findMany(SEARCH_FILMS_BY_DIRECTORS, searchPattern);
        } else if (searchBy.equals("title")) {
            films = findMany(SEARCH_FILMS_BY_TITLE, searchPattern);
        } else if (searchBy.contains("director") && searchBy.contains("title")) {
            films = findMany(SEARCH_FILMS_BY_DIRS_AND_FILM_NAME, searchPattern, searchPattern);
        }
        films.forEach(f -> {
            setLikesAndGenres(f);
            setDirectors(f);
        });
        return films;
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
    }

    private void checkGenres(Film film) {
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

    private void checkDirectors(Film film) {
        Set<Long> dirId = directorStorage.getAllDirectors()
                .stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        boolean isCorrectDirs = film.getDirectors()
                .stream()
                .map(Director::getId)
                .allMatch(dirId::contains);
        if (!isCorrectDirs) {
            throw new NotFoundException("Режиссеры не найдены");
        }
    }

    private void saveGenresToFilm(Film film, long filmId) {
        Set<Genre> filmGenre = film.getGenres();
        update(DEL_FILM_GENRES, filmId);
        if (filmGenre.isEmpty()) {
            return;
        }
        filmGenre.forEach(genre -> update(GENRES_TO_FILM, genre.getId(), filmId));
    }

    private void saveDirectorsToFilm(Film film, long filmId) {
        Set<Director> filmDirectors = film.getDirectors();
        update(DEL_FILM_DIRECTORS, filmId);
        if (filmDirectors.isEmpty()) {
            return;
        }
        filmDirectors.forEach(dir -> update(DIRECTORS_TO_FILM, dir.getId(), filmId));
    }

    private void setLikesAndGenres(Film film) {
        film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId())));
        film.setLikes(new HashSet<>(likeStorage.getLikes(film.getId())));
    }

    private void setDirectors(Film film) {
        film.setDirectors(new HashSet<>(directorStorage.getDirectorsByFilmId(film.getId())));
    }
}
