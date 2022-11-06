package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static java.time.Month.JANUARY;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creates_newUser_andStatusIs200() throws Exception {
        User user = new User();
        user.setName("asd");
        user.setEmail("asd@mail.ru");
        user.setLogin("logIn");
        user.setBirthday(LocalDate.now());

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));
    }

    @Test
    void updates_presentedUser_inUsersList_andStatusIs200() throws Exception {
        User u2 = new User();
        u2.setId(1);
        u2.setName("updatedUser");
        u2.setEmail("asd@mail.ru");
        u2.setLogin("logIn");
        u2.setBirthday(LocalDate.now());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(u2))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(200));

        mockMvc.perform(
                        get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(u2)));
    }

    @Test
    void when_User_loginIsEmpty_statusIs400() throws Exception {
        User u1 = new User();
        u1.setEmail("asd@mail.ru");
        u1.setBirthday(LocalDate.now());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(u1))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.login").value("must not be blank"));
    }

    @Test
    void when_User_emailIsNotCorrect_statusIs400() throws Exception {
        User u1 = new User();
        u1.setLogin("testUser");
        u1.setEmail("asdmail.ru");
        u1.setBirthday(LocalDate.now());

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(u1))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
    }

    @Test
    void when_User_birthdayIsInFuture_statusIs400() throws Exception {
        User u1 = new User();
        u1.setLogin("testUser");
        u1.setEmail("asd@mail.ru");
        u1.setBirthday(LocalDate.of(2023, 12, 22));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(u1))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.birthday").value("must be a date in the past or in the present"));
    }

    @Test
    void when_User_idIsNegative_statusIs404() throws Exception {
        User u1 = new User();
        u1.setId(-1);
        u1.setLogin("testUser");
        u1.setEmail("asd@mail.ru");
        u1.setBirthday(LocalDate.now());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(u1))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.id").value("must be positive"));
    }

    @Test
    void checkUserListNotNull() throws Exception {
        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    void checkFindUserById() throws Exception {
        User u1 = new User();
        u1.setId(1);
        u1.setName("user1");
        u1.setEmail("user@mail.ru");
        u1.setLogin("user1");
        u1.setBirthday(LocalDate.of(1985, JANUARY, 1));

        mockMvc.perform(
                        get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(u1)));
    }

    @Test
    void addToFriendList() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFromFriendList() throws Exception {
        mockMvc.perform(put("/users/2/friends/1"));

        mockMvc.perform(delete("/users/2/friends/1"))
                .andExpect(status().isOk());
    }

    @Test
    void checkFriendList_AndSizeIs1() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

    }

    @Test
    void createCommonFriends_andCommonFriendsNotEmpty() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(put("/users/3/friends/2"));
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
        mockMvc.perform(get("/users/1/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$..id").value(2));
    }
}