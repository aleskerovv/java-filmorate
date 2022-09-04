package ru.yandex.practicum.filmorate.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @SneakyThrows
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(initId());
        users.put(user.getId(), user);
        return user;
    }

    @SneakyThrows
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() < 0) {
            throw new UserValidationException();
        }
        users.put(user.getId(), user);
        return user;
    }

    private Integer initId() {
        List<Integer> idList = getUsers().stream()
                .map(User::getId)
                .sorted()
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            return 1;
        }
        return idList.get(idList.size() - 1) + 1;
    }
}
