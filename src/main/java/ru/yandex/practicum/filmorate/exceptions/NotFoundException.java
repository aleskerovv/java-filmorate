package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundException extends RuntimeException {
    private final String parameter;

    public NotFoundException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
