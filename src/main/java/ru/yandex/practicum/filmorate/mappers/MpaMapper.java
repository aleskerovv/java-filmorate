package ru.yandex.practicum.filmorate.mappers;


import ru.yandex.practicum.filmorate.model.MpaCategory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper {
    public static MpaCategory mapToMpa(ResultSet rs, int rowNumber) throws SQLException {
        int id = rs.getInt("mpa_rate_id");
        String name = rs.getString("name");

        MpaCategory category = new MpaCategory();
        category.setId(id);
        category.setName(name);

        return category;
    }
}
