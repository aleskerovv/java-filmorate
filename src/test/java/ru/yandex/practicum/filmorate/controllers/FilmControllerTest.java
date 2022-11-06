package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data-users-films.sql"})
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
        f.getMpa().setId(3);


        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));
    }

    @Test
    void updates_presentedFilm_andStatusIs200() throws Exception {
        Film f2 = new Film();
        f2.setId(1);
        f2.setName("first film upd");
        f2.setDescription("Desc of new film");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);
        f2.getMpa().setId(1);


        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(f2))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(200));

        f2.getMpa().setName("G");
        f2.setRate(0);

        mockMvc.perform(
                get("/films/1")
                        .content(objectMapper.writeValueAsString(f2))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(content().json(objectMapper.writeValueAsString(f2)));
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
    void when_FilmsId_isNegative_andStatusIs404() throws Exception {
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
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("{\"id\":\"must be positive\"}",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void films_ListIsNotEmpty_andStatusIs200() throws Exception {
        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    void addLikeToFilm_AndLikesCountIs1() throws Exception {
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1"))
                .andExpect(jsonPath("$.likes", hasSize(1)));
    }

    @Test
    void deleteLikes_andLikesCountIs0() throws Exception {
        mockMvc.perform(put("/films/2/like/1"));
        mockMvc.perform(delete("/films/2/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/2"))
                .andExpect(jsonPath("$.likes", hasSize(0)));
    }


    @Test
    void addLikeToFilm_whichNotPresent() throws Exception {
        mockMvc.perform(put("/films/15/like/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTop2Film() throws Exception {
        mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void getFilmsTopSortedByLikes() throws Exception {
        mockMvc.perform(put("/films/3/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/3/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular?count=1"))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$..description").value("test desc of third film"));
    }

    @Test
    @DisplayName("Check that film was found with correct reqParams")
    void getFoundFilmsWithCorrectByParam() throws Exception {
        mockMvc.perform(get("/films/search?query=film&by=title"))
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    @DisplayName("Check that film was not found with incorrect reqParams")
    void getFoundFilmsWithIncorrectByParam() throws Exception {
        mockMvc.perform(get("/films/search?query=film&by=query"))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("incorrect filter type",
                        result.getResponse().getContentAsString()));
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", "    "})
    @DisplayName("Check that film was not found with blank query")
    void getFoundFilmsWithBlankOrEmptyQueryParam(String param) throws Exception {
        mockMvc.perform(get("/films/search?query=" + param + "&by=query"))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("search string could not be blank",
                        result.getResponse().getContentAsString()));
    }

    @Test
    @DisplayName("Check that film was found without reqParams")
    void getFoundFilmsWithoutByParam() throws Exception {
        mockMvc.perform(get("/films/search?query=film&by=title"))
                .andExpect(jsonPath("$.*", hasSize(3)));
    }
}