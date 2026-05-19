package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingDbStorageTest {

    private final RatingDbStorage dbStorage;

    @Test
    void shouldGetAllRatings() {
        assertThat(dbStorage.getAllRatings().size()).isEqualTo(5);
    }

    @Test
    void shouldGetRatingById() {
        Rating rating = dbStorage.getRating(1L);
        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getName()).isEqualTo("G");
    }
}