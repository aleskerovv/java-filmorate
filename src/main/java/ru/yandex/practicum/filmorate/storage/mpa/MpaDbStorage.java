package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.model.MpaCategory;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@Component
public class MpaDbStorage implements AbstractStorage<MpaCategory> {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaCategory getById(int id) {
        String query = "select * from mpa_rating \n" +
                "where mpa_rate_id = ?";

        return jdbcTemplate.queryForObject(query, MpaMapper::mapToMpa, id);
    }

    @Override
    public List<MpaCategory> getAll() {
        String query = "select * from mpa_rating";

        return jdbcTemplate.query(query, MpaMapper::mapToMpa);
    }
}
