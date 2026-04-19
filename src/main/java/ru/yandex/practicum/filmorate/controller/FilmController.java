package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping
    public Collection<Film> getAll() {
        return service.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film newFilm) {
        //log.info("Добавлен фильм {}", newFilm);
        return service.createFilm(newFilm);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        //   log.info("Обновлен фильм {}", oldFilm);
        return service.updateFilm(newFilm);

    }

    @PutMapping("{id}/like/{userId}")
    public Film setLike(@PathVariable long id,
                        @PathVariable long userId) {
        //log.info("Пользователь с id={} поставил лайк фильму {}", userId, id);
        return service.setLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film delLike(@PathVariable long id,
                        @PathVariable long userId) {
        //log.info("Удален лайк пользователя с id={} с фильма {}", userId, id);
        return service.delLike(id, userId);
    }

    @GetMapping("popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return service.getPopularFilms(count);
    }
}
