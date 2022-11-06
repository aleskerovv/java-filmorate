package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractDictionary;

import java.util.List;

@Component
public class GenreDbStorage implements AbstractDictionary<Genre> {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getById(int id) {
        try {
            String query = "select * from genres \n" +
                    "where genre_id = ?";

            return jdbcTemplate.queryForObject(query, GenreMapper::mapToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("genre with id %d not found", id));
        }
    }

    @Override
    public List<Genre> getAll() {
        String query = "select * from genres";

        return jdbcTemplate.query(query, GenreMapper::mapToGenre);
    }
}
