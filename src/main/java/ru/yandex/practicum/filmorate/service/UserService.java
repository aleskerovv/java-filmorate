package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendsId) {
        if (userStorage.findById(id) == null) {
            log.error("user with id={} not found", id);
            throw new NotFoundException("id", String.format("user with id=%d not found", id));
        }
        if (userStorage.findById(friendsId) == null) {
            log.error("user with id={} not found", friendsId);
            throw new NotFoundException("id", String.format("user with id=%d not found", friendsId));
        }

        userStorage.addFriend(id, friendsId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriendsSet(Integer id) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be positive");
        }

        if (userStorage.findById(id) == null) {
            throw new NotFoundException("id", String.format("user with id=%d not found", id));
        }
        return userStorage.getFriendsSet(id);
    }

    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        return userStorage.getMutualFriendsSet(id, friendId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User findUserById(Integer id) {
        User user = userStorage.findById(id);
        if (user == null) throw new NotFoundException("id", String.format("user with id %d not found", id));
        return userStorage.findById(id);
    }

    public void deleteAllUsers() {
        userStorage.deleteAll();
    }
}
