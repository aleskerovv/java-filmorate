package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.findFilmById(filmId).getLikes()
                .add(userId);
        log.info("like for film with id={} successfully added", filmId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!filmStorage.findFilmById(filmId).getLikes().contains(userId)) {
            log.error("user with id={} not found", userId);
            throw  new NotFoundException("id","user with id not found");
        }
        filmStorage.findFilmById(filmId).getLikes()
                .remove(userId);
        log.info("like for film with id={} deleted successfully", filmId);
    }

    public List<Film> getFilmsTop(Integer count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
