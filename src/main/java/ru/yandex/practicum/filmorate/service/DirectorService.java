package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public Collection<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    public Director getDirector(long id) {
        return storage.getDirector(id);
    }

    public Director createDirector(Director director) {
        return storage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return storage.updateDirector(director);
    }

    public void delDirector(long id) {
        storage.delDirector(id);
    }
}
