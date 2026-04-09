package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void addCorrectFilm_returnFilmIdWithId() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = Film.builder()
                .name("IT")
                .description("about film")
                .duration(45)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();

        Film newFilm = filmStorage.createFilm(film);

        assertNotNull(newFilm.getId());
        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getDuration(), newFilm.getDuration());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
    }

    @Test
    void addFilmWithDescriptionWith201Length_returnError() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = Film.builder()
                .name("IT")
                .description("Описание 201 символОписание 201 символОписание 201 символОписание 201 символОписание " +
                        "201 символОписание 201 символОписание 201 символОписание 201 символОписание 201 символ" +
                        "Описание 201 символОписание 21")
                .duration(45)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        try {
            Film newFilm = filmStorage.createFilm(film);
        } catch (Exception e) {
            assertEquals("Описание не может быть более 200 символов", e.getMessage());
        }
    }

    @Test
    void addFilmWithoutParams_returnError() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = null;
        try {
            Film newFilm = filmStorage.createFilm(film);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("\"film\" is null"));
        }
    }

    @Test
    void addFilmWithEmptyName_returnError() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = Film.builder()
                .name("")
                .description("Описание")
                .duration(45)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        try {
            Film newFilm = filmStorage.createFilm(film);
        } catch (Exception e) {
            assertEquals("Название не может быть пустым", e.getMessage());
        }
    }

    @Test
    void addFilmWithReleaseDateAfter_returnError() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        final LocalDate startFilmReleaseDate = LocalDate.of(1895, 12, 28);
        Film film = Film.builder()
                .name("IT")
                .description("Описание")
                .duration(0)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();
        try {
            Film newFilm = filmStorage.createFilm(film);
        } catch (Exception e) {
            assertEquals("Дата релиза не может быть раньше " + startFilmReleaseDate, e.getMessage());
        }
    }

    @Test
    void addFilmWithNegativeDuration_returnError() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = Film.builder()
                .name("IT")
                .description("Описание")
                .duration(-1)
                .releaseDate(LocalDate.of(1895, 12, 29))
                .build();
        try {
            Film newFilm = filmStorage.createFilm(film);
        } catch (Exception e) {
            assertEquals("Продолжительность не может меньше 0", e.getMessage());
        }
    }

    @Test
    void updateFilm_returnFilmWithUpdates() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        Film film = filmStorage.createFilm(Film.builder()
                .name("IT")
                .description("about film")
                .duration(45)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build());

        Film updateFilm = Film.builder()
                .id(film.getId())
                .name("Penny")
                .description("about Penny")
                .duration(50)
                .releaseDate(LocalDate.of(2001, 2, 2))
                .build();

        Film newFilm = filmStorage.updateFilm(updateFilm);

        assertEquals(updateFilm.getName(), newFilm.getName());
        assertEquals(updateFilm.getDescription(), newFilm.getDescription());
        assertEquals(updateFilm.getDuration(), newFilm.getDuration());
        assertEquals(updateFilm.getReleaseDate(), newFilm.getReleaseDate());
    }

    @Test
    void addCorrectUser_returnUserWithId() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User user = User.builder()
                .name("John")
                .email("test@tet.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User newUser = userStorage.createUser(user);

        assertNotNull(newUser.getId());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    void addUserWithEmptyName_returnUserWithNameEqualLogin() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User user = User.builder()
                .name("")
                .email("test@tet.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User newUser = userStorage.createUser(user);

        assertNotNull(newUser.getId());
        assertEquals(newUser.getName(), newUser.getLogin());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    void updateUser_returnUserWithUpdates() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();

        User user = userStorage.createUser(User.builder()
                .name("John")
                .email("test@tet.ru")
                .login("login")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        User updateUser = User.builder()
                .id(user.getId())
                .name("Paul")
                .email("ololo@tet.ru")
                .login("newLogin")
                .birthday(LocalDate.of(2002, 3, 4))
                .build();

        User newUser = userStorage.updateUser(updateUser);

        assertEquals(updateUser.getName(), newUser.getName());
        assertEquals(updateUser.getEmail(), newUser.getEmail());
        assertEquals(updateUser.getBirthday(), newUser.getBirthday());
        assertEquals(updateUser.getLogin(), newUser.getLogin());
    }

    @Test
    void addUserWithIncorrectEmail_returnError() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User user = User.builder()
                .name("John")
                .email("test")
                .login("userLogin")
                .birthday(LocalDate.of(1999, 12, 29))
                .build();
        try {
            User newUser = userStorage.createUser(user);
        } catch (Exception e) {
            assertEquals("Email должен содержать '@'", e.getMessage());
        }
    }

    @Test
    void addUserWithEmptyLogin_returnError() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User user = User.builder()
                .name("John")
                .email("test@mail.ru")
                .login("")
                .birthday(LocalDate.of(1999, 12, 29))
                .build();
        try {
            User newUser = userStorage.createUser(user);
        } catch (Exception e) {
            assertEquals("Логин не может быть пустым", e.getMessage());
        }
    }

    @Test
    void addUserWithBirthdayInFuture_returnError() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        User user = User.builder()
                .name("John")
                .email("test@mail.ru")
                .login("newLogin")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        try {
            User newUser = userStorage.createUser(user);
        } catch (Exception e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
    }
}
