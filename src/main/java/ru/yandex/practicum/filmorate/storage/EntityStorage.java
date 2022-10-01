package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface EntityStorage<T> {
    List<T> getAll();

    T findById(Integer id);

    T create(T t);

    T update(T t);

    void deleteAll();
}
