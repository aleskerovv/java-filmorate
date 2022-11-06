package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Accessors(chain = true)
public class Review {
    @PositiveOrZero(message = "id must be positive")
    private Integer reviewId;

    @NotNull(message = "content can not be null")
    private String content;

    @NotNull(message = "isPositive can not be null")
    private Boolean isPositive;

    @NotNull(message = "userId can not be null")
    private Integer userId;

    @NotNull(message = "filmId can not be null")
    private Integer filmId;

    private Integer useful;
}
