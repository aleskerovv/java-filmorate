package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements EntityStorage<User> {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("id", String.format("User with id=%d not found", id));
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(initId());
        users.put(user.getId(), user);
        log.info("user with id={} was created", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("id", String.format("user with id=%d not found", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("user with id={} was updated", user.getId());
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    private Integer initId() {
        List<Integer> idList = getAll().stream()
                .map(User::getId)
                .sorted()
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            return 1;
        }
        return idList.get(idList.size() - 1) + 1;
    }
}