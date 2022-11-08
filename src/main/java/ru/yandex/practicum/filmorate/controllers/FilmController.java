package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Tag(name = "Operations with films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @Operation(summary = "Returns all films")
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    @Operation(summary = "returns film by id if exists")
    public Film findById(@PathVariable Integer id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    @Operation(summary = "creates new film")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    @Operation(summary = "updates film if exists")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @Operation(summary = "adds like to film from user if both exists")
    public void addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @Operation(summary = "deletes like to film from user if both exists")
    public void deleteLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    @Operation(summary = "returns top-N films by rate")
    public List<Film> getFilmsTop(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
                                  @RequestParam(value = "genreId", defaultValue = "-1", required = false) Integer genreId,
                                  @RequestParam(value = "year", defaultValue = "-1", required = false) Integer year) {
        return filmService.getFilmsTop(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    @Operation(summary = "deletes film by id if exists")
    public void deleteFilmById(@PathVariable Integer filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    @Operation(summary = "returns film by director")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    @Operation(summary = "returns films by filter")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam(defaultValue = "title") List<String> by) {
        return filmService.searchFilms(query, by);
    }
}
