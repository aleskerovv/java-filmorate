package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityStorage<User> userStorage;

    @BeforeEach
    void clear() {
        userStorage.deleteAll();
    }

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
        User u = new User();
        u.setName("asd");
        u.setEmail("asd@mail.ru");
        u.setLogin("logIn");
        u.setBirthday(LocalDate.now());

        User u2 = new User();
        u2.setId(1);
        u2.setName("updatedUser");
        u2.setEmail("asd@mail.ru");
        u2.setLogin("logIn");
        u2.setBirthday(LocalDate.now());

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
    void when_User_idIsNegative_statusIs400() throws Exception {
        User u1 = new User();
        u1.setId(-1);
        u1.setLogin("testUser");
        u1.setEmail("asd@mail.ru");
        u1.setBirthday(LocalDate.now());

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(u1))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("id cannot be negative",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void checkUserListNotNull() throws Exception {
        User u1 = new User();
        u1.setName("updatedUser");
        u1.setEmail("asd@mail.ru");
        u1.setLogin("logIn");
        u1.setBirthday(LocalDate.now());


        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        u1.setId(1);

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(u1))));
    }

    @Test
    void checkFindUserById() throws Exception {
        User u1 = new User();
        u1.setName("updatedUser");
        u1.setEmail("asd@mail.ru");
        u1.setLogin("logIn");
        u1.setBirthday(LocalDate.now());


        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        u1.setId(1);

        mockMvc.perform(
                        get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(u1)));
    }

    @Test
    void addToFriendList() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setBirthday(LocalDate.now());
        user.setEmail("new@mail.ru");

        User friend = new User();
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.now());
        friend.setEmail("friend@email.ru");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFromFriendList() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setBirthday(LocalDate.now());
        user.setEmail("new@mail.ru");

        User friend = new User();
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.now());
        friend.setEmail("friend@email.ru");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(put("/users/1/friends/2"));

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void checkFriendList_AndSizeIs1() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setBirthday(LocalDate.now());
        user.setEmail("new@mail.ru");

        User friend = new User();
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.now());
        friend.setEmail("friend@email.ru");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(put("/users/1/friends/2"));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

    }

    @Test
    void createCommonFriends_andCommonFriendsNotEmpty() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setBirthday(LocalDate.now());
        user.setEmail("new@mail.ru");

        User friend = new User();
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.now());
        friend.setEmail("friend@email.ru");

        User commonFriend = new User();
        commonFriend.setLogin("commonFriend");
        commonFriend.setBirthday(LocalDate.now());
        commonFriend.setEmail("common@mail.ru");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(commonFriend))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(put("/users/1/friends/3"));
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
        mockMvc.perform(get("/users/3/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void deleteAllUsers() throws Exception {
        User user = new User();
        user.setLogin("newUser");
        user.setBirthday(LocalDate.now());
        user.setEmail("new@mail.ru");

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(delete("/users"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users"))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }
}