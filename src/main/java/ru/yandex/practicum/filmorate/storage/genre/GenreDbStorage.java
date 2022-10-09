package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@Component
public class GenreDbStorage implements AbstractStorage<Genre> {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getById(int id) {
        String query = "select * from genre \n" +
                "where genre_id = ?";

        return jdbcTemplate.queryForObject(query, GenreMapper::mapToGenre, id);
    }

    @Override
    public List<Genre> getAll() {
        String query = "select * from genre";

        return jdbcTemplate.query(query, GenreMapper::mapToGenre);
    }
}
