package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenresController {
    private final GenreService genreService;

    @Autowired
    public GenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable int id) {
        return genreService.getById(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAll();
    }
}
