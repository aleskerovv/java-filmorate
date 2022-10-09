package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaCategory;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final AbstractStorage<MpaCategory> mpaStorage;

    public MpaController(AbstractStorage<MpaCategory> mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping("/{id}")
    public MpaCategory findCategoryById(@PathVariable int id) {
        return mpaStorage.getById(id);
    }

    @GetMapping
    public List<MpaCategory> getAllCategories() {
        return mpaStorage.getAll();
    }
}
