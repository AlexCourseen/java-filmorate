package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage dbStorage;

    @Test
    void shouldFindUserById() {
        User user = dbStorage.getUser(1L);
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Testov1");
        assertThat(user.getEmail()).isEqualTo("test1@test.com");
        assertThat(user.getLogin()).isEqualTo("test1");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(2001, 1, 1));
    }

    @Test
    void shouldCreateUserAndGetIt() {
        User user = new User();
        user.setName("newUser");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        user.setEmail("new@mail.com");

        User newUser = dbStorage.createUser(user);
        User userDb = dbStorage.getUser(newUser.getId());

        assertThat(userDb.getName()).isEqualTo("newUser");
        assertThat(userDb.getLogin()).isEqualTo("Login");
        assertThat(userDb.getBirthday()).isEqualTo(LocalDate.of(1999, 1, 1));
        assertThat(userDb.getEmail()).isEqualTo("new@mail.com");

        dbStorage.deleteUser(userDb.getId());
    }

    @Test
    void shouldFindAllUsers() {
        Collection<User> users = dbStorage.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void shouldUpdateUser() {
        User user = dbStorage.getUser(4);
        user.setName("update");
        user.setLogin("Updated");
        user.setBirthday(LocalDate.of(2000, 2, 2));
        user.setEmail("update@test.ru");
        User userUpdated = dbStorage.updateUser(user);

        assertThat(userUpdated.getName()).isEqualTo("update");
        assertThat(userUpdated.getLogin()).isEqualTo("Updated");
        assertThat(userUpdated.getBirthday()).isEqualTo(LocalDate.of(2000, 2, 2));
        assertThat(userUpdated.getEmail()).isEqualTo("update@test.ru");
    }

//
//    @Test
//    void shouldReturnPopularFilmsSortedByLikes() {
//        Collection<Film> films = filmStorage.getPopularFilms(3);
//        assertThat(films).hasSize(3);
//        assertThat(films)
//                .extracting(Film::getId)
//                .containsExactly(1L, 3L, 2L);
//    }
}