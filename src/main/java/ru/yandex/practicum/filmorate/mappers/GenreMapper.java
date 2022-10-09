package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper {
    public static Genre mapToGenre(ResultSet rs, int rowNumber) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");

        Genre genre = new Genre();
        genre.setGenreId(id);
        genre.setName(name);

        return genre;
    }
}
