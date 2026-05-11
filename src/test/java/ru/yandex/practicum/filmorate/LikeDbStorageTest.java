package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDbStorageTest {

    private final LikeDbStorage dbStorage;

    @Test
    void shouldGetAllLikesByFilmId() {
        Collection<Like> likes = dbStorage.getLikes(1);
        assertThat(likes.size()).isEqualTo(4);
    }

    @Test
    void shouldAddLike() {
        Collection<Like> likes = dbStorage.getLikes(4);
        assertThat(likes.size()).isEqualTo(0);
        dbStorage.addLike(4,3);
        Collection<Like> likesAfterAddLike = dbStorage.getLikes(4);
        assertThat(likesAfterAddLike.size()).isEqualTo(1);
    }

    @Test
    void shouldDelLike() {
        Collection<Like> likes = dbStorage.getLikes(4);
        assertThat(likes.size()).isEqualTo(1);
        dbStorage.delLike(4,3);
        Collection<Like> likesAfterDelLike = dbStorage.getLikes(4);
        assertThat(likesAfterDelLike.size()).isEqualTo(0);
    }
}