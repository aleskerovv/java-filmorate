package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

@Component("filmDbStorage")
public class FilmDbStorage implements EntityStorage<Film> {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public Film findById(Integer id) {
        String sql = "select * from films where id=?";

        return jdbcTemplate.queryForObject(sql, FilmMapper::mapToFilm, id);
    }

    @Override
    public Film create(Film film) {
        String sql = "insert into films (title, description, release_date, duration, rating) " +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating());

        return film;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
