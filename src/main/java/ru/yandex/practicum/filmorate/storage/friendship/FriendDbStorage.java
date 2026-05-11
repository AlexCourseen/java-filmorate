package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Repository("friendDbStorage")
public class FriendDbStorage extends BaseDbStorage<Friend> implements FriendStorage {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private static final String ADD_FRIEND = "MERGE INTO friends (user_id, friend_id) " +
            "KEY(user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String DEL_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS = "SELECT friend_id FROM friends WHERE user_id = ?";

    @Autowired
    FriendDbStorage(JdbcTemplate jdbc, FriendRowMapper mapper, @Qualifier("userDbStorage")UserStorage userStorage) {
        super(jdbc, mapper);
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(long userId, long idFriend) {
        checkUser(userId);
        checkUser(idFriend);
        jdbc.update(ADD_FRIEND, userId, idFriend);
    }

    @Override
    public void delFriend(long userId, long idFriend) {
        checkUser(userId);
        checkUser(idFriend);
        jdbc.update(DEL_FRIEND, userId, idFriend);
    }

    @Override
    public Collection<Friend> getUserFriends(long userId) {
        checkUser(userId);
        return findMany(GET_FRIENDS, userId);
    }

    private void checkUser(long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Юзер с id = " + userId + " не найден");
        }
    }
}
