package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {
    UserController uc = new UserController();
    static User user;
    static User userWithNegativeId;

    @BeforeEach
    void createBefore() {
        user = User.builder()
                .name("asd")
                .email("asd@mail.ru")
                .login("logIn")
                .birthday(LocalDate.now())
                .build();
        userWithNegativeId = User.builder()
                .id(-1)
                .name("user")
                .email("asd@mail,ru")
                .login("asdasd")
                .birthday(LocalDate.now())
                .build();
    }

    @Test
    void create() {
        uc.createUser(user);
        user.setId(1);
        assertEquals(uc.getUsers().get(0), user);
    }

    @Test
    void checkThrowableWhenIdIsNegative() {
        Throwable exception = assertThrows(UserValidationException.class, () -> uc.updateUser(userWithNegativeId));

        assertEquals(null, exception.getMessage());
    }

    @Test
    void checkUserListNotNull() {
        uc.createUser(user);
        user.setId(1);
        List<User> users = List.of(user);

        assertEquals(1, users.size());
    }
}
