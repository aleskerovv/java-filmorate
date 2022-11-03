package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {
    public static User mapToUser(ResultSet rs, int rowNumber) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"))
                .setEmail(rs.getString("email"))
                .setLogin(rs.getString("login"))
                .setName(rs.getString("name"))
                .setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }

}
