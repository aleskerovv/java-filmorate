package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        this.setFriendsId(users);

        return users;
    }

    @Override
    public User findById(Integer id) {
        try {
            String query = "select * from users where id = ?";
            User user = jdbcTemplate.queryForObject(query, UserMapper::mapToUser, id);
            this.setFriendsId(user);

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
        this.isUserExists(user.getId());

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
    public void addFriend(Integer id, Integer friendId) {
        this.isUserExists(id);
        this.isUserExists(friendId);
        String query = "merge into friendships(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        this.isUserExists(id);
        this.isUserExists(friendId);
        String query = "delete from friendships where user_id = ? and friend_id = ?";
        jdbcTemplate.update(query, id, friendId);
    }

    @Override
    public List<User> getFriendsSet(Integer id) {
        this.isUserExists(id);

        String query = "select friend_id from friendships where user_id = ?";
        List<Integer> idList = jdbcTemplate.queryForList(query, Integer.class, id);

        return idList.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        this.isUserExists(id);
        this.isUserExists(friendId);

        String query = "SELECT u.* FROM USERS u " +
                "JOIN friendships uf ON u.user_id = uf.FRIEND_ID " +
                "JOIN friendships f on u.user_id = f.FRIEND_ID " +
                "WHERE uf.friend_id = f.friend_id and uf.user_id=? and f.user_id=?";

        return jdbcTemplate.query(query, UserMapper::mapToUser, id, friendId);
    }

    private void setFriendsId(List<User> users) {
        Map<Integer, User> usersMap = new HashMap<>();
        users.forEach(user -> usersMap.put(user.getId(), user));

        String friendsQuery = "select * from friendships";
        jdbcTemplate.query(friendsQuery, rs -> {
            usersMap.get(rs.getInt("user_id")).addFriend(rs.getInt("friend_id"));
        });
    }

    private void setFriendsId(User user) {
        String friendsQuery = "select * from friendships where user_id = ?";
        jdbcTemplate.query(friendsQuery, rs -> {
            Optional<Integer> friendId = Optional.of(rs.getInt("friend_id"));
            friendId.ifPresent(user::addFriend);
        }, user.getId());
    }

    private void isUserExists(Integer id) {
        String sqlQuery = "select count(*) from users where id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (result != 1) {
            throw new NotFoundException("id", String
                    .format("user with id %d does not exists", id));
        }
    }
}
