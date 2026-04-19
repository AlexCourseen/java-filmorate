package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User getUser(long id) {
        return storage.getUser(id);
    }

    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User createUser(User newUser) {
        return storage.createUser(newUser);
    }

    public User updateUser(User newUser) {
        return storage.updateUser(newUser);
    }

    public void addFriend(long userId, long idFriend) {
        User user = getUser(userId);
        User userFriend = getUser(idFriend);
        user.getFriends().add(idFriend);
        userFriend.getFriends().add(userId);
    }

    public List<User> getFriends(long idUser) {
        return getUser(idUser).getFriends()
                .stream()
                .map(storage::getUser)
                .toList();
    }

    public List<User> delFriend(long userId, long idFriend) {
        User user = getUser(userId);
        User userFriend = getUser(idFriend);
        userFriend.getFriends().remove(userId);
        Set<Long> userFriends = user.getFriends();
        userFriends.remove(idFriend);
        return userFriends
                .stream()
                .map(storage::getUser)
                .toList();
    }

    public List<User> commonFriends(long userId, long otherId) {
        User user = getUser(userId);
        User otherUser = getUser(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(storage::getUser)
                .toList();
    }
}
