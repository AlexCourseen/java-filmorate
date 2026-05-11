package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.RatingRowMapper;

import java.util.Collection;

@Repository
public class RatingDbStorage extends BaseDbStorage<Rating> implements RatingStorage {
    private static final String FIND_ALL_RATINGS = "SELECT * FROM rating ORDER BY rating_id ASC";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM rating WHERE rating_id = ? ORDER BY rating_id ASC";

    @Autowired
    RatingDbStorage(JdbcTemplate jdbc, RatingRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Rating> getAllRatings() {
        return findMany(FIND_ALL_RATINGS);
    }

    @Override
    public Rating getRating(long id) {
        return findOne(FIND_RATING_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }
}
