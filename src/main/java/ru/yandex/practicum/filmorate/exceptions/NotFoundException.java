package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, code = HttpStatus.NOT_FOUND)
@Slf4j
public class NotFoundException extends RuntimeException {
    private final String parameter;

    public NotFoundException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
        log.warn(message);
    }

    public String getParameter() {
        return parameter;
    }
}
