package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_DIRECTORS =
            "SELECT * FROM directors";
    private static final String FIND_DIRECTOR_BY_ID =
            "SELECT * FROM directors WHERE director_id = ?";
    private static final String CREATE_DIRECTOR =
            "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name=? WHERE director_id = ?";
    private static final String DEL_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String GET_DIRS_BY_FILM_ID =
            "SELECT d.director_id, d.name " +
                    "FROM directors d " +
                    "JOIN film_director fd ON fd.director_id=d.director_id " +
                    "WHERE fd.film_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    @Override
    public Director getDirector(long id) {
        return findOne(FIND_DIRECTOR_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден"));
    }

    @Override
    public Director createDirector(Director director) {
        checkDirector(director);
        String name = director.getName();
        long id = insert(CREATE_DIRECTOR, name);
        return getDirector(id);
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirector(director);
        long id = director.getId();
        if (getDirector(id) != null) {
            String newName = director.getName();
            update(UPDATE_DIRECTOR, newName, id);
            return getDirector(id
            );
        }
        throw new NotFoundException("Режиссер с id = " + director.getId() + " не найден");
    }

    @Override
    public Collection<Director> getDirectorsByFilmId(long id) {
        return findMany(GET_DIRS_BY_FILM_ID, id);
    }

    @Override
    public void delDirector(long id) {
        update(DEL_DIRECTOR, id);
    }

    private void checkDirector(Director director) {
        if (director.getName().isBlank() || director.getName() == null) {
            throw new ValidationException("Имя не может быть пустым");
        }
    }
}