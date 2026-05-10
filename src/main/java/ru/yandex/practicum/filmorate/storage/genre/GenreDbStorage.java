package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_GENRES = "SELECT * FROM genre ORDER BY genre_id ASC";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ? ORDER BY genre_id ASC";
    private static final String FIND_GENRES_BY_FILM_ID =
            "SELECT g.genre_id, g.name " +
                    "FROM genre g " +
                    "JOIN film_genre fg ON fg.genre_id=g.genre_id " +
                    "WHERE fg.film_id = ?" +
                    " ORDER BY g.genre_id ASC";

    GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return findMany(FIND_ALL_GENRES);
    }

    @Override
    public Genre getGenre(long id) {
        return findOne(FIND_GENRE_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }

    @Override
    public Collection<Genre> getGenresByFilmId(long id) {
        return findMany(FIND_GENRES_BY_FILM_ID, id);
    }
}
