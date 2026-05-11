package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FriendStorage friendStorage;
    @Qualifier("userDbStorage")
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
        friendStorage.addFriend(userId,idFriend);
    }

    public Collection<Friend> getUserFriends(long idUser) {
        return friendStorage.getUserFriends(idUser);
    }

    public void delFriend(long userId, long idFriend) {
        friendStorage.delFriend(userId,idFriend);
    }

    public List<Friend> commonFriends(long userId, long otherId) {
        Collection<Friend> userFriends = friendStorage.getUserFriends(userId);
        Collection<Friend> otherUserFriends = friendStorage.getUserFriends(otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .toList();
    }
}
