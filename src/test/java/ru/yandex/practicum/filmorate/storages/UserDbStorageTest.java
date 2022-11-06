package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void test_FindById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.findById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void test_FindAllUsers() {
        Optional<List<User>> usersList = Optional.ofNullable(userStorage.getAll());

        assertThat(usersList)
                .isPresent();

        assertEquals(3, usersList.get().size());
    }

    @Test
    void test_CreateUserWithoutName() {
        User user = new User();
        user.setLogin("user4");
        user.setEmail("test4@mail.ru");
        user.setBirthday(LocalDate.now());

        userStorage.create(user);
        user.setId(4);

        assertEquals(user, userStorage.findById(4));
    }

    @Test
    void test_userNotFound() {
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> userStorage.findById(5));
        String message = "user with id 5 not found";

        assertThat(nfe.getMessage())
                .isEqualTo(message);
    }

    @Test
    void test_updateUser() {
        Optional<User> userToUpdate = Optional.ofNullable(userStorage.findById(1));

        assertThat(userToUpdate)
                .isPresent();
        userToUpdate.get().setEmail("updatedMail@mail.ru");

        userStorage.update(userToUpdate.get());

        assertThat(userToUpdate)
                .contains(userStorage.findById(1));
    }

    @Test
    void test_addFriend() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);

        assertThat(userStorage.getFriendsSet(1))
                .hasSize(1);
        assertThat(userStorage.getFriendsSet(2))
                .hasSize(1);
    }

    @Test
    void test_getMutualFriend() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);

        assertThat(userStorage.getFriendsSet(1))
                .isEqualTo(userStorage.getFriendsSet(3));

        assertThat(userStorage.getMutualFriendsSet(1, 3))
                .contains(userStorage.findById(2));
    }

    @Test
    void test_deleteFriends() {
        userStorage.addFriend(1, 2);

        assertThat(userStorage.getFriendsSet(1))
                .isNotEmpty();

        userStorage.deleteFriend(1, 2);
        assertThat(userStorage.getFriendsSet(1))
                .isEmpty();
    }

    @Test
    void test_deleteUser() {
        userStorage.deleteById(1);
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> userStorage.findById(1));
        String message = "user with id 1 not found";

        assertThat(nfe.getMessage())
                .isEqualTo(message);
    }


}