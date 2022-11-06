package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

public interface FilmStorage extends EntityStorage<Film> {
    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
    List<Film> getFilmsTop(Integer count);
    List<Film> getFilmsByDirector(int directorId, String sortBy);
    List<Film> searchFilmByTitle(String filter);
}
