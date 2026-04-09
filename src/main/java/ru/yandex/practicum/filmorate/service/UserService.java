package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
        Set<Long> userFriends = checkFriends(userId);
        Set<Long> friendFriends = checkFriends(idFriend);
        userFriends.add(idFriend);
        friendFriends.add(userId);
    }

    public List<User> getFriends(long idUser) {
        return checkFriends(idUser)
                .stream()
                .map(storage::getUser)
                .toList();
    }

    public List<User> delFriend(long userId, long idFriend) {
        Set<Long> userFriends = checkFriends(userId);
        Set<Long> friendFriends = checkFriends(idFriend);
        userFriends.remove(idFriend);
        friendFriends.remove(userId);
        return userFriends.stream()
                .map(storage::getUser)
                .toList();
    }

    public List<User> commonFriends(long userId, long otherId) {
        Set<Long> userFriends = checkFriends(userId);
        Set<Long> otherUserFriends = checkFriends(otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(storage::getUser)
                .toList();
    }

    private Set<Long> checkFriends(long id) {
        User user = getUser(id);
        return Optional.ofNullable(user.getFriends())
                .orElseGet(() -> {
                    Set<Long> friends = new HashSet<>();
                    user.setFriends(friends);
                    return friends;
                });
    }
}
