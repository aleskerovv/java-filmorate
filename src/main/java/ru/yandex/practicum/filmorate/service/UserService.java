package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final EntityStorage<User> userStorage;

    @Autowired
    public UserService(EntityStorage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendsId) {
        User user = userStorage.findById(id);
        User anotherUser = userStorage.findById(friendsId);

        if (user == null) {
            log.error("user with id={} not found", id);
            throw new NotFoundException("id", String.format("user with id=%d not found", id));
        }
        if (anotherUser == null) {
            log.error("user with id={} not found", friendsId);
            throw new NotFoundException("id", String.format("user with id=%d not found", friendsId));
        }

        if (!user.getFriends().contains(friendsId)) {
            user.addFriend(friendsId);
            anotherUser.addFriend(id);
            log.info("user with id {} added to friends list user with id {}", id, friendsId);
        } else {
            log.info("user with id {} already friend with id {}", id, friendsId);
        }
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.findById(id).deleteFriend(friendId);
        userStorage.findById(friendId).deleteFriend(id);
        log.info("user with id {} deleted from friends list user with id {}", id, friendId);
    }

    public List<User> getFriendsSet(Integer id) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be positive");
        }

        if (userStorage.findById(id) == null) {
            throw new NotFoundException("id", String.format("user with id=%d not found", id));
        }
        return userStorage.findById(id).getFriends()
                .stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        Set<Integer> usersFriendsSet = new HashSet<>(userStorage.findById(id).getFriends());
        Set<Integer> friendsSet = new HashSet<>(userStorage.findById(friendId).getFriends());
        return usersFriendsSet.stream()
                .filter(friendsSet::contains)
                .map(userStorage::findById)
                .collect(Collectors.toList());
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
        return userStorage.findById(id);
    }

    public void deleteAllUsers() {
        userStorage.deleteAll();
    }
}
