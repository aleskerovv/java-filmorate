package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"file:src/main/resources/schema.sql", "file:src/main/resources/data.sql"})
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creates_newFilm_andStatusIs200() throws Exception {
        Film f = new Film();
        f.setName("New film");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);
        f.setRate(5);
        f.getMpa().setId(1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));
    }

    @Test
    void updates_presentedFilm_andStatusIs200() throws Exception {
        Film f = new Film();
        f.setName("New film");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);
        f.setRate(5);
        f.getMpa().setId(1);
        Film f2 = new Film();
        f2.setId(1);
        f2.setName("New film upd");
        f2.setDescription("Desc of new film");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);
        f2.setRate(5);
        f2.getMpa().setId(1);
        f2.getMpa().setName("G");

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(f2))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(f2)));
    }

    @Test
    void when_Films_nameIsEmpty_andStatusIs400() throws Exception {
        Film f = new Film();
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.name").value("can not be blank"));
    }

    @Test
    void when_FilmsDescription_lengthIsAbove200_andStatusIs400() throws Exception {
        Film f = new Film();
        f.setName("Test film");
        f.setDescription("l".repeat(250));
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.description").value("length must be between 1 and 200"));
    }

    @Test
    void when_FilmsReleaseDate_isBeforeCinemaBirthday_andStatusIs400() throws Exception {
        Film f = new Film();
        f.setName("Test film");
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.of(1800, Month.DECEMBER, 1));
        f.setDuration(50);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.releaseDate").value("must be after 28-DEC-1895"));
    }

    @Test
    void when_FilmsDuration_isNegative_andStatusIs400() throws Exception {
        Film f = new Film();
        f.setName("Test film");
        f.setDescription("desc");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(-50);

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.duration").value("duration can not be negative"));
    }

    @Test
    void when_FilmsId_isNegative_andStatusIs400() throws Exception {
        Film f = new Film();
        f.setId(-1);
        f.setName("Test film");
        f.setDescription("test desc");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("id cannot be negative",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void films_ListIsNotEmpty_andStatusIs200() throws Exception {
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);
        f.getMpa().setName("G");

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(f))));
    }

    @Test
    void addLikeToFilm_AndLikesCountIs1() throws Exception {
        User u1 = new User();
        u1.setName("updatedUser");
        u1.setEmail("asd@mail.ru");
        u1.setLogin("logIn");
        u1.setBirthday(LocalDate.now());
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(1)));
    }

    @Test
    void deleteLikes_andLikesCountIs0() throws Exception {
        User u1 = new User();
        u1.setName("updatedUser");
        u1.setEmail("asd@mail.ru");
        u1.setLogin("logIn");
        u1.setBirthday(LocalDate.now());

        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);

        mockMvc.perform(put("/films/1/like/1"));
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(0)));
    }


    @Test
    void addLikeToFilm_whichNotPresent() throws Exception {
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTop2Film() throws Exception {
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);

        Film f2 = new Film();
        f2.setName("New f2ilm upd");
        f2.setDescription("Desc of2 new f2ilm");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);
        f2.setRate(5);
        f2.getMpa().setId(1);

        Film f3 = new Film();
        f3.setName("New film upd");
        f3.setDescription("Desc of new film");
        f3.setReleaseDate(LocalDate.now());
        f3.setDuration(25);
        f3.setRate(5);
        f3.getMpa().setId(1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f2))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f2.setId(2);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f3))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f3.setId(3);

        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void getFilmsTopSortedByLikes() throws Exception {
        User u1 = new User();
        u1.setName("updatedUser");
        u1.setEmail("asd@mail.ru");
        u1.setLogin("logIn");
        u1.setBirthday(LocalDate.now());

        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);

        Film f2 = new Film();
        f2.setName("New f2ilm upd");
        f2.setDescription("Desc of2 new f2ilm");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);
        f2.setRate(5);
        f2.getMpa().setId(2);

        Film f3 = new Film();
        f3.setName("New film upd");
        f3.setDescription("Desc of new film");
        f3.setReleaseDate(LocalDate.now());
        f3.setDuration(25);
        f3.setRate(5);
        f3.getMpa().setId(3);

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(u1))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);
        f.getMpa().setName("G");



        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f2))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f2.setId(2);
        f2.getMpa().setName("PG");

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f3))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f3.setId(3);
        f3.getMpa().setName("PG-13");

        mockMvc.perform(put("/films/3/like/1"))
                .andExpect(status().isOk());
        f3.addLike(1);
        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(f3, f))));
    }

    @Test
    void deleteAllFilms() throws Exception {
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);
        f.setRate(5);
        f.getMpa().setId(1);
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        mockMvc.perform(delete("/films"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films"))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));

    }
}
