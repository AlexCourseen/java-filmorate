package ru.yandex.practicum.filmorate.storage.recomendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository()
public class RecommendationDbStorage extends BaseDbStorage<Film> implements RecommendationStorage {

    private final FilmDbStorage filmDbStorage;
    private static final String FIND_SIMILAR_USER = "SELECT l2.user_id FROM likes l1 JOIN likes l2 ON " +
            "l1.film_id = l2.film_id WHERE l1.user_id = ? AND l2.user_id != ? " +
            "GROUP BY l2.user_id ORDER BY COUNT(*) DESC LIMIT 1";

    private static final String FIND_RECOMMENDATIONS = "SELECT f.film_id, " +
            "f.name, f.description, f.releasedate AS releaseDate, f.duration, " +
            "f.rating_id, r.name AS mpa_name FROM film f LEFT JOIN rating r " +
            "ON f.rating_id = r.rating_id WHERE f.film_id IN (SELECT l.film_id FROM likes l " +
            "WHERE l.user_id = ? AND l.film_id NOT IN (SELECT l2.film_id FROM likes l2 " +
            "WHERE l2.user_id = ?)) ORDER BY f.film_id";

    private static final String HAS_LIKES = "SELECT COUNT(*) > 0 FROM likes WHERE user_id = ?";

    @Autowired
    public RecommendationDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmDbStorage filmDbStorage) {
        super(jdbc, mapper);
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Collection<Film> getRecommendations(long userId) {
        if (!hasLikes(userId)) {
            return Collections.emptyList();
        }

        List<Long> similarUsers = jdbc.queryForList(FIND_SIMILAR_USER, Long.class, userId, userId);

        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Long similarUserId = similarUsers.get(0);

        Collection<Film> films = findMany(FIND_RECOMMENDATIONS, similarUserId, userId);
        films.forEach(film -> filmDbStorage.setLikesAndGenres(film));
        return films;
    }

    private boolean hasLikes(long userId) {
        Boolean has = jdbc.queryForObject(HAS_LIKES, Boolean.class, userId);
        return has != null && has;
    }


}
