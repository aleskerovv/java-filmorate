package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        userStorage.findById(userId);
        filmStorage.addLike(filmId, userId);
        log.info("like for film with id={} added", filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        userStorage.findById(userId);
        filmStorage.findById(filmId);
        filmStorage.deleteLike(filmId, userId);
        log.info("like for film with id={} deleted", filmId);
    }

    public List<Film> getFilmsTop(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException("field 'count' must be positive");
        }
        return filmStorage.getFilmsTop(count);
    }


    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
    }

    public Film findFilmById(Integer id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }
}
