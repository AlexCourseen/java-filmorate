package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.LikeRowMapper;

import java.util.Collection;

@Repository("likeDbStorage")
public class LikeDbStorage extends BaseDbStorage<Like> implements LikeStorage {
    private final static String ADD_LIKE = "MERGE INTO likes (film_id, user_id) " +
            "KEY(film_id, user_id) " +
            "VALUES (?, ?)";
    private final static String DEL_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";

    LikeDbStorage(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE, filmId, userId);
    }

    @Override
    public void delLike(long filmId, long userId) {
        jdbc.update(DEL_LIKE, filmId, userId);
    }

    @Override
    public Collection<Like> getLikes(long filmId) {
        return findMany(GET_LIKES, filmId);
    }
}
