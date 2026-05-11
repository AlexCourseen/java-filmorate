package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.friendship.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {

    private final FriendDbStorage dbStorage;

    @Test
    void shouldAddFriendOneSidedAndGetFriends() {
        dbStorage.addFriend(1, 2);
        List<Friend> friendsUser1 = new ArrayList<>(dbStorage.getUserFriends(1));
        assertThat(friendsUser1.size()).isEqualTo(1);
        assertThat(friendsUser1.get(0).getId()).isEqualTo(2);
        assertThat(dbStorage.getUserFriends(2).isEmpty());
    }

    @Test
    void shouldDelFriend() {
        dbStorage.addFriend(1, 2);
        dbStorage.addFriend(1, 3);
        List<Friend> friends = new ArrayList<>(dbStorage.getUserFriends(1));
        assertThat(friends.size()).isEqualTo(2);
        dbStorage.delFriend(1, 2);
        List<Friend> friendsAfterDel = new ArrayList<>(dbStorage.getUserFriends(1));
        assertThat(friendsAfterDel.size()).isEqualTo(1);
        assertThat(friendsAfterDel.get(0).getId()).isEqualTo(3);
    }
}