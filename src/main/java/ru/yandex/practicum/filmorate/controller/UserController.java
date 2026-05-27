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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<User> getAll() {
        return service.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody @Valid User newUser) {
        //log.info("Создан пользователь фильм {}", newUser);
        return service.createUser(newUser);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        // log.info("Обновлен пользователь {}", oldUser);
        return service.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        service.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<Friend> getFriends(@PathVariable long id) {
        return service.getUserFriends(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void delFriend(@PathVariable long id,
                                @PathVariable long friendId) {
        service.delFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Friend> getCommonFriends(@PathVariable long id,
                                       @PathVariable long otherId) {
        return service.commonFriends(id, otherId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Удаление пользователя с id={}", userId);
        service.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Получение пользователя с id={}", userId);
        return service.getUser(userId);
    }
}
