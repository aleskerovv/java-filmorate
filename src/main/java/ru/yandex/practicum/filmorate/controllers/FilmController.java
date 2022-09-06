package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(initId());
        films.put(film.getId(), film);
        log.info("film with id={} created successfully", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        films.put(film.getId(), film);
        log.info("film with id={} updated successfully", film.getId());
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
