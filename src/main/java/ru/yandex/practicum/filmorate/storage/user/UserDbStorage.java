package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

@Repository("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String CREATE_USER = "INSERT INTO users(name, email, login, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String DEL_USER = "DELETE FROM users WHERE user_id = ?";


    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAllUsers() {
        return findMany(FIND_ALL_USERS);
    }

    @Override
    public User getUser(long id) {
        return findOne(FIND_USER_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Юзер с id = " + id + " не найден"));
    }

    @Override
    public User createUser(User user) {
        checkUser(user);
        Object[] params = new Object[]{
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        };
        long id = insert(CREATE_USER, params);
        return getUser(id);
    }

    @Override
    public User updateUser(User user) {
        long userId = user.getId();
        if (getUser(userId) != null) {
            Object[] params = new Object[]{
                    user.getName(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getId()
            };
            update(UPDATE_USER, params);
            return getUser(user.getId());
        }
        throw new NotFoundException("Юзер с id = " + userId + " не найден");
    }

    public void deleteUser(long id) {
        getUser(id);
        update(DEL_USER, id);
    }

    private void checkUser(User user) {
        if (user.getLogin().isBlank() || user.getLogin() == null) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
