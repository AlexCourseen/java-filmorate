package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.Collection;

public interface FriendStorage {
    void addFriend(long user1Id, long user2Id);

    void delFriend(long user1Id, long user2Id);

    Collection<Friend> getUserFriends(long userId);
}