package ru.yandex.practicum.filmorate.controllers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @SneakyThrows
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException();
        }
        film.setId(initId());
        films.put(film.getId(), film);
        return film;
    }

    @SneakyThrows
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
                || film.getId() < 0) {
            throw new FilmValidationException();
        }
        films.put(film.getId(), film);
        return film;
    }

    private Integer initId() {
        List<Integer> idList = getFilms().stream()
                .map(Film::getId)
                .sorted()
                .collect(Collectors.toList());

        if (films.isEmpty()) {
            return 1;
        }
        return idList.get(idList.size() - 1) + 1;
    }
}
