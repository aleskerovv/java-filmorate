package ru.yandex.practicum.filmorate.exceptions;

public class DuplicateEventException extends BadRequestException {
    public DuplicateEventException(String message) {
        super(message);
    }
}
