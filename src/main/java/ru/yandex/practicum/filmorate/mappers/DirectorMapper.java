package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper {

    public static Director mapToDirector(ResultSet rs, int rowNumber) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        Director director = new Director();
        director.setId(id);
        director.setName(name);
        return director;
    }
}
