package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final DirectorService directorService;

    @Override
    public List<Film> getAll() {
        String query = "SELECT f.*, mr.name as mpa_name " +
                "FROM films f " +
                "left join MPA_RATING MR on f.MPA_RATE_ID = MR.MPA_RATE_ID " +
                "ORDER BY f.id";
        List<Film> films = jdbcTemplate.query(query, FilmMapper::mapToFilm);
        this.setAttributes(films);

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

            this.setAttributes(film);

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("film with id %d not found", id));
        }
    }

    @Override
    public Film create(Film film) {
        String createQuery = "insert into films(name, description, release_date, duration, mpa_rate_id) \n" +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        Optional<Integer> id = Optional.of(keyHolder.getKey().intValue());
        film.setId(id.get());

        setGenres(film);

        setDirectorsToFilm(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        this.isFilmExists(film.getId());

        String deleteGenres = "delete from films_genres where film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());

        String query = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_rate_id = ?" +
                " where id = ?";

        jdbcTemplate.update(query, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        setGenres(film);

        setDirectorsToFilm(film);

        return film;
    }

    private void setDirectorsToFilm(Film film) {
        String sqlQuery = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            sqlQuery = "MERGE INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";

            jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    private void setGenres(Film film) {
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            String genresQuery = "merge into films_genres(film_id, genre_id) \n" +
                    "values (?, ?)";

            jdbcTemplate.batchUpdate(genresQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });

        }
    }

    @Override
    public void deleteById(Integer id) {
        this.isFilmExists(id);
        String query = "delete from films where id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        this.isFilmExists(filmId);
        String query = "merge into films_likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(query, filmId, userId);

        String updateFilmRate = "update films \n " +
                "set rate = rate + 1 \n " +
                "where id = ?";
        jdbcTemplate.update(updateFilmRate, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        this.isFilmExists(filmId);
        String query = "delete from films_likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(query, filmId, userId);

        String updateFilmRate = "update films \n " +
                "set rate = rate - 1 \n " +
                "where id = ?";
        jdbcTemplate.update(updateFilmRate, filmId);
    }

    @Override
    public List<Film> getFilmsTop(Integer count) {
        String query = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATE, f.MPA_RATE_ID, mr.name as mpa_name \n " +
                "FROM FILMS f \n " +
                "left join MPA_RATING MR on f.MPA_RATE_ID = MR.MPA_RATE_ID \n " +
                "ORDER BY f.rate DESC, f.ID " +
                "LIMIT ? ";

        List<Film> filmsSorted = jdbcTemplate.query(query, FilmMapper::mapToFilm, count);

        this.setAttributes(filmsSorted);

        return filmsSorted;
    }

    @Override
    public List<Film> searchFilmByTitle(String filter) {
        String sqlQuery = "SELECT f.*, mr.name as mpa_name " +
                "FROM films f " +
                "JOIN mpa_rating mr ON f.mpa_rate_id = mr.mpa_rate_id " +
                "WHERE LOWER(f.name) LIKE LOWER(?)" +
                "ORDER BY f.rate DESC";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmMapper::mapToFilm, "%"+filter+"%");
        this.setAttributes(films);

        return films;
    }

    private void setAttributes(List<Film> films) {
        Map<Integer, Film> filmMap = new HashMap<>();
        films.forEach(film -> filmMap.put(film.getId(), film));

        if (!filmMap.isEmpty()) {

            String genres = "SELECT fg.film_id, g.* " +
                    "FROM films_genres fg " +
                    "LEFT JOIN genres g on fg.genre_id = g.genre_id " +
                    "ORDER BY fg.FILM_ID, fg.GENRE_ID";

            jdbcTemplate.query(genres, rs -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));
                Optional.ofNullable(filmMap.get(rs.getInt("film_id")))
                        .ifPresent(f -> f.getGenres().add(genre));
            });

            String directorsSql = "SELECT FD.FILM_ID, D.* " +
                    "FROM FILMS_DIRECTORS FD " +
                    "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID = D.ID " +
                    "ORDER BY FD.FILM_ID, FD.DIRECTOR_ID";

            jdbcTemplate.query(directorsSql, rs -> {
               Director director = new Director();
               director.setId(rs.getInt("id"));
               director.setName(rs.getString("name"));
               Optional.ofNullable(filmMap.get(rs.getInt("film_id")))
                       .ifPresent(f -> f.getDirectors().add(director));
            });

            String likes = "SELECT * " +
                    "FROM films_likes";

            jdbcTemplate.query(likes, rs -> {
                Integer userId = rs.getInt("user_id");
                Optional.ofNullable(filmMap.get(rs.getInt("film_id")))
                        .ifPresent(f -> f.addLike(userId));
            });
        }
    }

    private void setAttributes(Film film) {
        String genres = "SELECT fg.film_id, g.* " +
                "FROM films_genres fg " +
                "LEFT JOIN genres g on fg.genre_id = g.genre_id " +
                "WHERE fg.FILM_ID = ?" +
                "ORDER BY fg.FILM_ID, fg.GENRE_ID";

        jdbcTemplate.query(genres, rs -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            film.getGenres().add(genre);
        }, film.getId());

        String likes = "SELECT * " +
                "FROM films_likes " +
                "WHERE FILM_ID = ?";

        jdbcTemplate.query(likes, rs -> {
            film.addLike(rs.getInt("user_id"));
        }, film.getId());

        film.setDirectors(new HashSet<>(directorService.getDirectorsByFilmId(film.getId())));
    }

    private void isFilmExists(Integer id) {
        String sqlQuery = "select count(*) from films where id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (result != 1) {
            throw new NotFoundException("id", String
                    .format("film with id %d does not exists", id));
        }
    }


    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        List<Film> films;
        switch (sortBy) {
            case "year":
                String sqlQueryYear = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATE, F.MPA_RATE_ID, MR.NAME as MPA_NAME\n" +
                        "FROM FILMS F\n" +
                        "LEFT JOIN MPA_RATING MR on F.MPA_RATE_ID = MR.MPA_RATE_ID\n" +
                        "LEFT JOIN FILMS_DIRECTORS FD on F.ID = FD.FILM_ID\n" +
                        "WHERE FD.DIRECTOR_ID = ?\n" +
                        "ORDER BY F.RELEASE_DATE ASC, F.ID";
                films = jdbcTemplate.query(sqlQueryYear, FilmMapper::mapToFilm, directorId);
                break;
            case "likes":
                String sqlQueryLikes = "SELECT F.*, MR.NAME as MPA_NAME\n" +
                        "FROM FILMS F\n" +
                        "LEFT JOIN MPA_RATING MR on F.MPA_RATE_ID = MR.MPA_RATE_ID\n" +
                        "LEFT JOIN FILMS_DIRECTORS FD on F.ID = FD.FILM_ID\n" +
                        "LEFT OUTER JOIN FILMS_LIKES FL on F.ID = FL.FILM_ID\n" +
                        "WHERE FD.DIRECTOR_ID = ?\n" +
                        "GROUP BY F.ID, MR.NAME\n" +
                        "ORDER BY COUNT(FL.USER_ID) DESC, F.ID";
                films = jdbcTemplate.query(sqlQueryLikes, FilmMapper::mapToFilm, directorId);
                break;
            default:
                throw new IllegalArgumentException("Invalid request parameter");
        }
        this.setAttributes(films);
        return films;
    }
}