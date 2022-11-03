package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.annotations.CorrectLogin;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class User {
    @PositiveOrZero(message = "must be positive")
    private int id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @CorrectLogin
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void deleteFriend(Integer id) {
        friends.remove(id);
    }
}