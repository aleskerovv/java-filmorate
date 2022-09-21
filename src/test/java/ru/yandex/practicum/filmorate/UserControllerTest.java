package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creates_newUser_andStatusIs200() throws Exception {
        User user = User.builder()
                .name("asd")
                .email("asd@mail.ru")
                .login("logIn")
                .birthday(LocalDate.now())
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(200));
    }

    @Test
    void updates_presentedUser_inUsersList_andStatusIs200() throws Exception {
        User u = User.builder()
                .name("asd")
                .email("asd@mail.ru")
                .login("logIn")
                .birthday(LocalDate.now())
                .build();

        User u2 = User.builder()
                .id(1)
                .name("updatedUser")
                .email("asd@mail.ru")
                .login("logIn")
                .birthday(LocalDate.now())
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(u2))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(u2)));

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(u2))));
    }

    @Test
    void when_User_loginIsEmpty_statusIs400() throws Exception {
        User u1 = User.builder()
                .email("asd@mail.ru")
                .birthday(LocalDate.now())
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'login' must not be blank]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_User_emailIsNotCorrect_statusIs400() throws Exception {
        User u1 = User.builder()
                .login("testUser")
                .email("asdmail.ru")
                .birthday(LocalDate.now())
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'email' must be a well-formed email address]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_User_birthdayIsInFuture_statusIs400() throws Exception {
        User u1 = User.builder()
                .login("testUser")
                .email("asd@mail.ru")
                .birthday(LocalDate.of(2023, 12, 22))
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'birthday' must be a date in the past or in the present]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_User_idIsNegative_statusIs400() throws Exception {
        User u1 = User.builder()
                .id(-1)
                .login("testUser")
                .email("asd@mail.ru")
                .birthday(LocalDate.now())
                .build();

        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'id' must be positive]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void checkUserListNotNull() throws Exception {
        User u1 = User.builder()
                .id(1)
                .name("updatedUser")
                .email("asd@mail.ru")
                .login("logIn")
                .birthday(LocalDate.now())
                .build();


        mockMvc.perform(
                put("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(u1))));
    }
}