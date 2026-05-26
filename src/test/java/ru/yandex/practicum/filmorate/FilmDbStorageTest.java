package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("New film description");
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        film.setDuration(120);
        film.setMpa(createMpa(1));
        film.setGenres(Set.of(createGenre(1), createGenre(2)));
        return film;
    }

    private Rating createMpa(Integer id) {
        Rating rating = new Rating();
        rating.setId(id.longValue());
        return rating;
    }

    private Genre createGenre(Integer id) {
        Genre genre = new Genre();
        genre.setId(id.longValue());
        return genre;
    }

    @Test
    void shouldFindFilmById() {
        Film film = filmStorage.getFilm(1L);
        assertThat(film.getId()).isEqualTo(1L);
        assertThat(film.getName()).isEqualTo("Титаник");
        assertThat(film.getDescription()).isEqualTo("Фильм про корабль");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2000, 3, 31));
        assertThat(film.getDuration()).isEqualTo(136);
        assertThat(film.getMpa().getId()).isEqualTo(4);
    }

    @Test
    void shouldCreateFilmAndGetIt() {
        Film newFilm = filmStorage.createFilm(createFilm("Nem film"));
        Film filmDb = filmStorage.getFilm(newFilm.getId());

        assertThat(filmDb.getName()).isEqualTo("Nem film");
        assertThat(filmDb.getDescription()).isEqualTo("New film description");
        assertThat(filmDb.getReleaseDate()).isEqualTo(LocalDate.of(1999, 1, 1));
        assertThat(filmDb.getDuration()).isEqualTo(120);
        assertThat(filmDb.getMpa().getId()).isEqualTo(1);

        filmStorage.deleteFilm(filmDb.getId());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = filmStorage.getFilm(3);
        film.setName("update");
        film.setDescription("Updated");
        film.setReleaseDate(LocalDate.of(2000, 2, 2));
        film.setDuration(150);
        film.setMpa(createMpa(2));
        Film filmUpdated = filmStorage.updateFilm(film);

        assertThat(filmUpdated.getName()).isEqualTo("update");
        assertThat(filmUpdated.getDescription()).isEqualTo("Updated");
        assertThat(filmUpdated.getReleaseDate()).isEqualTo(LocalDate.of(2000, 2, 2));
        assertThat(filmUpdated.getDuration()).isEqualTo(150);
        assertThat(filmUpdated.getMpa().getId()).isEqualTo(2);
    }

    @Test
    void shouldFindAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        assertThat(films).hasSize(4);
    }

    @Test
    void shouldReturnPopularFilmsSortedByLikes() {
        Collection<Film> films = filmStorage.getPopularFilms(3);
        assertThat(films).hasSize(3);
        assertThat(films)
                .extracting(Film::getId)
                .containsExactly(1L, 3L, 2L);
    }
}