package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final EntityStorage<Film> filmStorage;
    private final EntityStorage<User> userStorage;

    @Autowired
    public FilmService(EntityStorage<Film> filmStorage, EntityStorage<User> userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.findById(filmId).addLike(userStorage.findById(userId).getId());
        log.info("like for film with id={} added", filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.findById(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("id",String.format("user with id=%d not found", userId));
        }
        film.deleteLike(userId);
        log.info("like for film with id={} deleted", filmId);
    }

    public List<Film> getFilmsTop(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException("field 'count' must be positive");
        }
        return filmStorage.getAll()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
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
