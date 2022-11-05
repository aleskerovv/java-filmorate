package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("id", String.format("film with id=%d not found", id));
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(initId());
        films.put(film.getId(), film);
        log.info("film with id={} was created", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() < 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("id", String.format("film with id=%d not found", film.getId()));
        }

        films.put(film.getId(), film);
        log.info("film with id={} was updated", film.getId());
        return film;
    }

    @Override
    public void deleteById(Integer id) {
        films.remove(id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        films.get(filmId).addLike(userId);
        log.info("like for film with id={} added", filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("id",String.format("user with id=%d not found", userId));
        }
        film.deleteLike(userId);
        log.info("like for film with id={} deleted", filmId);
    }

    @Override
    public List<Film> getFilmsTop(Integer count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Integer initId() {
        List<Integer> idList = getAll().stream()
                .map(Film::getId)
                .sorted()
                .collect(Collectors.toList());

        if (films.isEmpty()) {
            return 1;
        }
        return idList.get(idList.size() - 1) + 1;
    }

    @Override
    public List<Film> searchFilmByTitle(String query) {
        throw new UnsupportedOperationException("this type of operation not allowed");
    }
}
