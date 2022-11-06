package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final EventService eventService;
    private static final String TABLE_NAME = "users";

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public void addFriend(Integer id, Integer friendId) {
        userStorage.addFriend(id, friendId);
        eventService.addNewEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.ADD, TABLE_NAME);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.deleteFriend(id, friendId);
        eventService.addNewEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.REMOVE, TABLE_NAME);
    }

    public List<User> getFriendsSet(Integer id) {
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
        return userStorage.findById(id);
    }

    public void deleteUserById(Integer id) {
        userStorage.deleteById(id);
    }
}
