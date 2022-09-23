package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User findUserById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    void deleteAll();
}
