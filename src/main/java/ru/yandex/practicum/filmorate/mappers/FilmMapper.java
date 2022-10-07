package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class FilmMapper {
    public static Film mapToFilm(ResultSet rs, int rowNumber) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("title");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer rating = rs.getInt("rating");
        Integer mpaRate = rs.getInt("mpa_rate_id");

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setRating(rating);
        film.setMpaRating(mpaRate);

        return film;
    }
}