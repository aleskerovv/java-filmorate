package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@RestController
@RequestMapping("/genre")
public class GenresController {
    private final AbstractStorage<Genre> genreDbStorage;

    @Autowired
    public GenresController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable int id) {
        return genreDbStorage.getById(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreDbStorage.getAll();
    }
}
