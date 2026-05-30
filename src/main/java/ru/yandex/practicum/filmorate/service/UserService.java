package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendStorage;
import ru.yandex.practicum.filmorate.storage.recomendation.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FriendStorage friendStorage;
    @Qualifier("userDbStorage")
    private final UserStorage storage;
    private final EventStorage eventStorage;
    private final RecommendationStorage recommendationStorage;

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
        friendStorage.addFriend(userId, idFriend);
        eventStorage.addEvent(userId, EventType.FRIEND, Operation.ADD, idFriend);
    }

    public Collection<User> getUserFriends(long idUser) {
        return friendStorage.getUserFriends(idUser)
                .stream()
                .map(Friend::getId)
                .map(this::getUser)
                .toList();
    }

    public void delFriend(long userId, long idFriend) {
        friendStorage.delFriend(userId, idFriend);
        eventStorage.addEvent(userId, EventType.FRIEND, Operation.REMOVE, idFriend);
    }

    public List<User> commonFriends(long userId, long otherId) {
        Collection<Friend> userFriends = friendStorage.getUserFriends(userId);
        Collection<Friend> otherUserFriends = friendStorage.getUserFriends(otherId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(Friend::getId)
                .map(this::getUser)
                .toList();
    }

    public Collection<Event> getEvents(long userId) {
        return eventStorage.getAllEvents(userId);
    }


    public void deleteUser(long userId) {
        storage.deleteUser(userId);
    }

    public Collection<Film> getFilmRecommendation(long userId) {
        storage.getUser(userId);
        return recommendationStorage.getRecommendations(userId);
    }
}
