package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaCategory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper {
    public static Film mapToFilm(ResultSet rs, int rowNumber) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setReleaseDate(rs.getDate("release_date").toLocalDate())
                .setDuration(rs.getInt("duration"))
                .setRate(rs.getInt("rate"))
                .setMpa(new MpaCategory());
        film.getMpa().setId(rs.getInt("mpa_rate_id"));
        film.getMpa().setName(rs.getString("mpa_name"));

        return film;
    }
}
