package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public Collection<Director> getAll() {
        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable long id) {
        return service.getDirector(id);
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director newDirector) {
        return service.createDirector(newDirector);
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director newDirector) {
        return service.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}")
    public void delDirector(@PathVariable long id) {
        service.delDirector(id);
    }
}