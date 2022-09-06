package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotations.CorrectReleaseDay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @PositiveOrZero
    private int id;
    @NotNull @NotBlank
    private String name;
    @Length(min = 1, max = 200)
    private String description;
    @CorrectReleaseDay(message = "must be after 28-DEC-1895")
    private LocalDate releaseDate;
    @PositiveOrZero(message = "duration can not be negative")
    private int duration;
}
