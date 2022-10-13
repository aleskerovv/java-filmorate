package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String query = "select * from users";
        List<User> users = jdbcTemplate.query(query, UserMapper::mapToUser);
        users.forEach(user -> {
            String friendsQuery = "select friend_id from friendships where user_id = ?";
            List<Integer> friends = jdbcTemplate.queryForList(friendsQuery, Integer.class, user.getId());
            user.setFriends(new HashSet<>(friends));
        });

        return users;
    }

    @Override
    public User findById(Integer id) {
        try {
            String query = "select * from users where id = ?";
            User user = jdbcTemplate.queryForObject(query, UserMapper::mapToUser, id);

            String friendsQuery = "select friend_id from friendships where user_id = ?";
            List<Integer> friends = jdbcTemplate.queryForList(friendsQuery, Integer.class, id);
            user.setFriends(new HashSet<>(friends));
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("user with id %d not found", id));
        }
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int id = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(user)).intValue();
        user.setId(id);

        log.info("created user with id {}", user.getId());

        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        if (this.findById(user.getId()) == null) {
            throw new NotFoundException("id", String.format("user with id=%d not found", user.getId()));
        }
        String query = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where id = ?";
        jdbcTemplate.update(query, user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return findById(user.getId());
    }

    @Override
    public void deleteAll() {
        String query = "delete from users";
        jdbcTemplate.update(query);
    }

    @Override
    public void addFriend(Integer id, Integer friendsId) {
        String query = "merge into friendships(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(query, id, friendsId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        String query = "delete from friendships where user_id = ? and friend_id = ?";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public List<User> getFriendsSet(Integer id) {
        List<User> users = new ArrayList<>();
        try {
            String query = "select friend_id from friendships where user_id = ?";
            List<Integer> idList = jdbcTemplate.queryForList(query, Integer.class, id);
            users = idList.stream()
                    .map(this::findById)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            e.getMessage();
        }
        return users;
    }

    @Override
    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        List<User> mutualFriends = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY \n " +
                    "FROM USERS u \n " +
                    "WHERE u.ID IN (SELECT DISTINCT f.FRIEND_ID \n " +
                    "               from FRIENDSHIPS f \n " +
                    "                        inner join (select FRIEND_ID from FRIENDSHIPS where USER_ID = ?) uf " +
                    "on uf.FRIEND_ID = f.FRIEND_ID \n " +
                    "               where USER_ID = ?)";
            mutualFriends = jdbcTemplate.queryForStream(query, UserMapper::mapToUser, id, friendId)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            e.getMessage();
        }

        return mutualFriends;
    }
}
