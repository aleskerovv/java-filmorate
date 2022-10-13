package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaCategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class FilmMapper {
    public static Film mapToFilm(ResultSet rs, int rowNumber) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer rating = rs.getInt("rate");
        int mpa = rs.getInt("mpa_rate_id");
        String mpaName = rs.getString("mpa_name");

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setRate(rating);
        film.setMpa(new MpaCategory());
        film.getMpa().setId(mpa);
        film.getMpa().setName(mpaName);

        return film;
    }
}
