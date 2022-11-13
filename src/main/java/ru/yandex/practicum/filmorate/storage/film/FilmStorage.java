package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SearchParam;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

public interface FilmStorage extends EntityStorage<Film> {

    List<Film> getAll();

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getFilmsTop(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Integer> getRecommendations(Integer[] usersWithSimilarInterests, Integer idRecommendedUser,
                                     Integer limit);

    List<Film> searchFilm(String filter, List<SearchParam> params);

    List<Film> getCommonFilms(Integer userId, Integer friendsId);
}
