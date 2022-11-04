package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        String sqlQuery = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sqlQuery, DirectorMapper::mapToDirector);
    }

    @Override
    public Director findById(Integer id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, DirectorMapper::mapToDirector, id);
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO DIRECTORS (NAME) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Director with id = {} added", director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteById(Integer id) {

    }
}
