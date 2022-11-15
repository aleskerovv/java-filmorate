package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAll() {
        log.info("Getting a list of all directors");
        return directorStorage.getAll();
    }

    public Director findById(int id) {
        log.info("Getting director by id = {}", id);
        return directorStorage.findById(id);
    }

    public Director create(Director director) {
        log.info("Creating a director");
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        log.info("Updating director with id = {}", director.getId());
        return directorStorage.update(director);
    }

    public void delete(int id) {
        log.info("Delete director with id = {}", id);
        directorStorage.deleteById(id);
    }

    public List<Director> getDirectorsByFilmId(int id) {
        return directorStorage.getDirectorsByFilmId(id);
    }
}
