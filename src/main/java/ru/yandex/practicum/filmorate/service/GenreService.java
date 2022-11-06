package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.AbstractDictionary;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final AbstractDictionary<Genre> genreStorage;

    @Autowired
    public GenreService(AbstractDictionary<Genre> genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        log.info("find genre by id {}", id);
        return genreStorage.getById(id);
    }

    public List<Genre> getAll() {
        log.info("find all genres");
        return genreStorage.getAll();
    }
}
