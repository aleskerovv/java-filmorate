package ru.yandex.practicum.filmorate.storage;

public interface EntityStorage<T> {

    T findById(Integer id);

    T create(T t);

    T update(T t);

    void deleteById(Integer id);
}
