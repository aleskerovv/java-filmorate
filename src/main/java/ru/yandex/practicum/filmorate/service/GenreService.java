package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final AbstractStorage<Genre> genreStorage;

    @Autowired
    public GenreService(AbstractStorage<Genre> genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        return genreStorage.getById(id);
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}
