package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

public interface UserStorage extends EntityStorage<User> {
    void addFriend(Integer id, Integer friendsId);
    void deleteFriend(Integer id, Integer friendId);
    List<User> getFriendsSet(Integer id);
    List<User> getMutualFriendsSet(Integer id, Integer friendId);
}
