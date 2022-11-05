package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director findById(@PathVariable int id) {
        return directorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int id) {
        directorService.delete(id);
    }
}
