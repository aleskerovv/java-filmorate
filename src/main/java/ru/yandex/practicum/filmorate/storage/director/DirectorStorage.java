package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

public interface DirectorStorage extends EntityStorage<Director> {
    List<Director> getDirectorsByFilmId(int id);
}
