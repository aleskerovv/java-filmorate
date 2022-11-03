package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Review {
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
