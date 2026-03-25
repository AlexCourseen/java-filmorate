package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate startFilmReleaseDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film newFilm) {
        checkFilm(newFilm);
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Добавлен фильм {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            checkFilm(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm = newFilm;
            log.info("Обновлен фильм {}", oldFilm);
            return oldFilm;
        }
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long maxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private void checkFilm(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.warn("Ошибка валидации: название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: описание более 200 символов");
            throw new ValidationException("Описание не может быть более 200 символов");
        }
        if (film.getDuration() < 0) {
            log.warn("Ошибка валидации: продолжительность не может меньше 0");
            throw new ValidationException("Продолжительность не может меньше 0");
        }
        if (film.getReleaseDate().isBefore(startFilmReleaseDate)) {
            log.warn("Ошибка валидации: дата релиза не может быть раньше {}", startFilmReleaseDate);
            throw new ValidationException("Дата релиза не может быть раньше " + startFilmReleaseDate);
        }
    }
}
