package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
        if (!films.containsKey(film.getId())) {
            log.info("film with id={} not found", film.getId());
            throw new FilmValidationException(String.format("film with id=%d not found", film.getId()));
        }
        films.put(film.getId(), film);
        log.info("film with id={} updated successfully", film.getId());
        return film;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = "field '" + fieldName + "' " +
                            error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        log.warn(errors.values().toString());
        return errors.values().toString();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FilmValidationException.class)
    public String handleCustomValidationExceptions(
            FilmValidationException ex) {
        return ex.getMessage();
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
