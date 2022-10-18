package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaCategory;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final AbstractStorage<MpaCategory> mpaStorage;

    @Autowired
    public MpaService(AbstractStorage<MpaCategory> mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MpaCategory getById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        log.info("find mpa category by id {}", id);
        return mpaStorage.getById(id);
    }

    public List<MpaCategory> getAll() {
        log.info("find all mpa categories");
        return mpaStorage.getAll();
    }
}
