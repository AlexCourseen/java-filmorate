package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director getDirector(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Collection<Director> getDirectorsByFilmId(long id);

    void delDirector(long id);
}
