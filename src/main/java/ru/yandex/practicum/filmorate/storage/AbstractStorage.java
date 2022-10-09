package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface AbstractStorage<T> {
    T getById(int id);
    List<T> getAll();
}
