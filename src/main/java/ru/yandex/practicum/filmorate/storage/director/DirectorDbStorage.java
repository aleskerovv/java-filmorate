package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

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
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, DirectorMapper::mapToDirector, id)).get();
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("Director with id = %d does not exist", id));
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        int id = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(director)).intValue();
        director.setId(id);
        log.info("Director with id = {} added", director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        findById(director.getId());
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteById(Integer id) {
        findById(id);
        String sqlQuery = "DELETE DIRECTORS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Director> getDirectorsByFilmId(int id) {
        String sqlQuery = "SELECT D.* FROM FILMS_DIRECTORS FD " +
                "INNER JOIN DIRECTORS D on D.ID = FD.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ? " +
                "ORDER BY ID";
        return jdbcTemplate.query(sqlQuery, DirectorMapper::mapToDirector, id);
    }
}
