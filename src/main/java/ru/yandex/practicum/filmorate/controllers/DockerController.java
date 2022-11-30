package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String helloDocker() {
        return "Hello from Docker updated new version";
    }
}
