package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SearchParam;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.enums.SearchParam.searchParams;

@Service
@Slf4j
public class FilmService {

    private static final String TABLE_NAME = "films";
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       DirectorStorage directorStorage,
                       EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
    }

    public void addLike(Integer filmId, Integer userId) {
        userStorage.findById(userId);
        filmStorage.addLike(filmId, userId);
        eventService.addNewEvent(userId, filmId, Event.EventType.LIKE, Event.Operation.ADD, TABLE_NAME);
        log.info("like for film with id={} added", filmId);

    }

    public void deleteLike(Integer filmId, Integer userId) {
        userStorage.findById(userId);
        filmStorage.deleteLike(filmId, userId);
        eventService.addNewEvent(userId, filmId, Event.EventType.LIKE, Event.Operation.REMOVE, TABLE_NAME);
        log.info("like for film with id={} deleted", filmId);
    }

    public List<Film> getFilmsTop(Integer count, Integer genreId, Integer year) {
        if (count < 0) {
            throw new IllegalArgumentException("field 'count' must be positive");
        }
        return filmStorage.getFilmsTop(count, genreId, year);
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void deleteFilmById(Integer id) {
        filmStorage.deleteById(id);
    }

    public Film findFilmById(Integer id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        directorStorage.findById(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> searchFilms(String filter, List<String> by) {
        if (filter.isBlank()) {
            return filmStorage.getAll();
        }

        if (searchParams().containsAll(by)) {
            List<SearchParam> params = by.stream()
                    .map(sp -> SearchParam.valueOf(sp.toUpperCase()))
                    .collect(Collectors.toList());

            return filmStorage.searchFilm(filter, params);

        } else {
            throw new IllegalArgumentException("incorrect filter type");
        }
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendsId) {
        userStorage.findById(userId);
        userStorage.findById(friendsId);

        return filmStorage.getCommonFilms(userId, friendsId);
    }
}
