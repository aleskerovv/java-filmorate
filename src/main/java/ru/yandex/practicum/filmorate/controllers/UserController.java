package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(initId());
        users.put(user.getId(), user);
        log.info("user with id={} created successfully", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.info("user with id={} not found", user.getId());
            throw new UserValidationException(String.format("user with id=%d not found", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("user with id={} updated successfully", user.getId());
        return user;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = "field '" + fieldName + "' " +
                            error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        log.warn(errors.values().toString());
        return errors.values().toString();
    }

    Integer initId() {
        List<Integer> idList = getUsers().stream()
                .map(User::getId)
                .sorted()
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            return 1;
        }
        return idList.get(idList.size() - 1) + 1;
    }
}
