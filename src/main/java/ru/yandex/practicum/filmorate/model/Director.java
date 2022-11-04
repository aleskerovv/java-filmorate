package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
public class Director {
    @PositiveOrZero(message = "must be positive")
    int id;
    @NotBlank(message = "can not be blank")
    String name;
}