package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("userInMemoryStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
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
    public void deleteById(Integer id) {
        users.remove(id);
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

    @Override
    public void addFriend(Integer id, Integer friendsId) {
        if (!users.get(id).getFriends().contains(friendsId)) {
            users.get(id).addFriend(friendsId);
            users.get(friendsId).addFriend(id);
            log.info("user with id {} added to friends list user with id {}", id, friendsId);
        } else {
            log.info("user with id {} already friend with id {}", id, friendsId);
        }
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        users.get(id).deleteFriend(friendId);
        users.get(friendId).deleteFriend(id);
        log.info("user with id {} deleted from friends list user with id {}", id, friendId);
    }

    @Override
    public List<User> getFriendsSet(Integer id) {
        return users.get(id).getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        Set<Integer> usersFriendsSet = new HashSet<>(users.get(id).getFriends());
        Set<Integer> friendsSet = new HashSet<>(users.get(friendId).getFriends());
        return usersFriendsSet.stream()
                .filter(friendsSet::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }
}
