package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final EventService eventService;

    private final FilmStorage filmStorage;

    private static final String TABLE_NAME = "users";

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventService eventService, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.eventService = eventService;
        this.filmStorage = filmStorage;
    }

    public void addFriend(Integer id, Integer friendId) {
        userStorage.addFriend(id, friendId);
        eventService.addNewEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.ADD, TABLE_NAME);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.deleteFriend(id, friendId);
        eventService.addNewEvent(id, friendId, Event.EventType.FRIEND, Event.Operation.REMOVE, TABLE_NAME);
    }

    public List<User> getFriendsSet(Integer id) {
        return userStorage.getFriendsSet(id);
    }

    public List<User> getMutualFriendsSet(Integer id, Integer friendId) {
        return userStorage.getMutualFriendsSet(id, friendId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User findUserById(Integer id) {
        return userStorage.findById(id);
    }

    public void deleteUserById(Integer id) {
        userStorage.deleteById(id);
    }

    public List<Film> getRecommendations(Integer idRecommendedUser, Integer limitFilms) {

        if (getIdUsersWithSimilarInterests(idRecommendedUser).isEmpty()) return new ArrayList<>();
        List<Integer> usersWithSimilarInterests = getIdUsersWithSimilarInterests(idRecommendedUser);
        log.info("Compiled a list of users with similar interests " + usersWithSimilarInterests);
        List<Integer> idRecommendationsFilms = idsFilmsRecommendations(usersWithSimilarInterests,
                idRecommendedUser, limitFilms);
        List<Film> recommendationsFilms = filmsByIDFromList(idRecommendationsFilms);
        return recommendationsFilms;
    }

    public List<Integer> getIdUsersWithSimilarInterests(int id) {
        return userStorage.getIdUsersWithSimilarInterests(id);
    }

    public List<Film> filmsByIDFromList(List<Integer> ids) {
        List<Film> films = new ArrayList<>();
        for (Integer i : ids) {
            films.add(filmStorage.findById(i));
            log.info("Added to list film id " + i);
        }
        return films;
    }

    public List<Integer> idsFilmsRecommendations(List<Integer> usersWithSimilarInterests,
                                                 Integer idRecommendedUser, Integer limit) {
        List<Integer> filmsRecomendations = new ArrayList<>();
        for (Integer i : usersWithSimilarInterests) {
            log.info("Get recommendations from the user id " + i);
            List<Integer> idFilmsRecommendedByUser = filmStorage.getRecommendations(i,
                    idRecommendedUser);
            log.info("User id " + i + " recommends movies " + idFilmsRecommendedByUser);
            for (int j = 0; (j < idFilmsRecommendedByUser.size()) && (filmsRecomendations.size() < limit); j++) {
                Integer idFilm = idFilmsRecommendedByUser.get(j);
                if (!filmsRecomendations.contains(idFilm)) {
                    filmsRecomendations.add(idFilm);
                    log.info("Film id " + idFilm + " added to recommendation list");
                }
            }
        }
        return filmsRecomendations;
    }
}
