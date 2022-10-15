package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String query = "SELECT f.*, mr.name as mpa_name \n " +
                "FROM films f \n " +
                "left join MPA_RATING MR on f.MPA_RATE_ID = MR.MPA_RATE_ID \n";
        List<Film> films = jdbcTemplate.query(query, FilmMapper::mapToFilm);
        films.forEach(film -> {
            String genresQuery = "select fg.GENRE_ID, g2.NAME from FILMS_GENRES fg \n " +
                    "inner join GENRES G2 on fg.GENRE_ID = G2.GENRE_ID \n " +
                    "where fg.FILM_ID = ?";
            Set<Genre> genres = new HashSet<>(jdbcTemplate.query(genresQuery, GenreMapper::mapToGenre, film.getId()));
            film.setGenres(genres);
        });
        return films;
    }

    @Override
    public Film findById(Integer id) {
        try {
            String filmQuery = "select f.*, mr.NAME as mpa_name \n" +
                    " from FILMS f \n" +
                    " left join MPA_RATING MR on f.MPA_RATE_ID = MR.MPA_RATE_ID \n" +
                    " where id = ?";
            Film film = jdbcTemplate.queryForObject(filmQuery, FilmMapper::mapToFilm, id);

            String genresQuery = "select fg.GENRE_ID, g2.NAME from FILMS_GENRES fg \n " +
                    "inner join GENRES G2 on fg.GENRE_ID = G2.GENRE_ID \n " +
                    "where fg.FILM_ID = ?";
            Set<Genre> genres = new HashSet<>(jdbcTemplate.query(genresQuery, GenreMapper::mapToGenre, id));
            film.setGenres(genres);

            String likesQuery = "select user_id from films_likes where film_id = ? order by user_id asc";
            film.setLikes(new HashSet<>(jdbcTemplate.queryForList(likesQuery, Integer.class, id)));

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("film with id %d not found", id));
        }
    }

    @Override
    public Film create(Film film) {
        String createQuery = "insert into films(name, description, release_date, duration, rate, mpa_rate_id) \n" +
                "values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);


        Optional<Integer> id = Optional.of(keyHolder.getKey().intValue());
        film.setId(id.get());

        if (!film.getGenres().isEmpty()) {
            String query = "merge into films_genres(film_id, genre_id) \n" +
                    "values (?, ?)";
            film.getGenres()
                    .forEach(genre -> jdbcTemplate.update(query, film.getId(), genre.getId()));
        }


        return film;
    }

    @Override
    public Film update(Film film) {
        this.findById(film.getId());

        String deleteGenres = "delete from films_genres where film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());

        String query = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_rate_id = ?" +
                " where id = ?";
        jdbcTemplate.update(query, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());

        if (!film.getGenres().isEmpty()) {
            String genresQuery = "merge into films_genres(film_id, genre_id) \n" +
                    "values (?, ?)";
            film.getGenres()
                    .forEach(genre -> jdbcTemplate.update(genresQuery, film.getId(), genre.getId()));
        }

        return findById(film.getId());
    }

    @Override
    public void deleteAll() {
        String query = "delete from films cascade";
        jdbcTemplate.update(query);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        try {
            String query = "merge into films_likes(film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(query, filmId, userId);
        } catch (DataAccessException e) {
            e.getMessage();
        }

    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String query = "delete from films_likes where film_id = ? and user_id = ?";

        jdbcTemplate.update(query, filmId, userId);
    }

    @Override
    public List<Film> getFilmsTop(Integer count) {
        List<Film> filmsSorted = new ArrayList<>();
        try {
            String query = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATE, f.MPA_RATE_ID, mr.name as mpa_name \n " +
                    "FROM FILMS f \n " +
                    "left join MPA_RATING MR on f.MPA_RATE_ID = MR.MPA_RATE_ID \n " +
                    "LEFT JOIN FILMS_LIKES fl ON f.ID = fl.FILM_ID\n " +
                    "GROUP BY f.ID \n " +
                    "ORDER BY count(fl.FILM_ID) DESC, f.ID ASC \n " +
                    "LIMIT ? ";
            filmsSorted = jdbcTemplate.query(query, FilmMapper::mapToFilm, count);

            filmsSorted.forEach(film -> {
                String likesQuery = "select user_id from films_likes where film_id = ? order by user_id asc";
                film.setLikes(new HashSet<>(jdbcTemplate.queryForList(likesQuery, Integer.class, film.getId())));

                String genresQuery = "select fg.GENRE_ID, g2.NAME from FILMS_GENRES fg \n " +
                        "inner join GENRES G2 on fg.GENRE_ID = G2.GENRE_ID \n " +
                        "where fg.FILM_ID = ?";
                Set<Genre> genres = new HashSet<>(jdbcTemplate.query(genresQuery, GenreMapper::mapToGenre, film.getId()));
                film.setGenres(genres);
            });
        } catch (DataAccessException e) {
            e.getMessage();
        }
        return filmsSorted;
    }
}