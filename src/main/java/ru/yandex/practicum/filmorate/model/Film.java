package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.annotations.CorrectReleaseDay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
public class Film {
    @PositiveOrZero(message = "must be positive")
    private int id;
    @NotBlank(message = "can not be blank")
    private String name;
    @Length(min = 1, max = 200, message = "length must be between 1 and 200")
    private String description;
    @CorrectReleaseDay(message = "must be after 28-DEC-1895")
    private LocalDate releaseDate;
    @PositiveOrZero(message = "duration can not be negative")
    private int duration;
    private Set<Integer> likes = new HashSet<>();

    public void addLike(Integer id) {
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        likes.remove(id);
    }
}
