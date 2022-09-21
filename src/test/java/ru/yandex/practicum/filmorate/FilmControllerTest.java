package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creates_newFilm_andStatusIs200() throws Exception {
        Film f = Film.builder()
                .name("New film")
                .description("Desc of new film")
                .releaseDate(LocalDate.now())
                .duration(50)
                .build();


        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));
    }

    @Test
    void updates_presentedFilm_andStatusIs200() throws Exception {
        Film f = Film.builder()
                .name("New film")
                .description("Desc of new film")
                .releaseDate(LocalDate.now())
                .duration(50)
                .build();
        Film f2 = Film.builder()
                .id(1)
                .name("New film upd")
                .description("Desc of new film")
                .releaseDate(LocalDate.now())
                .duration(25)
                .build();

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
        Film f = Film.builder()
                .description("Desc of new film")
                .releaseDate(LocalDate.now())
                .duration(50)
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'name' must not be blank]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_FilmsDescription_lengthIsAbove200_andStatusIs400() throws Exception {
        Film f = Film.builder()
                .name("Test film")
                .description("l".repeat(250))
                .releaseDate(LocalDate.now())
                .duration(50)
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'description' length must be between 1 and 200]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_FilmsReleaseDate_isBeforeCinemaBirthday_andStatusIs400() throws Exception {
        Film f = Film.builder()
                .name("Test film")
                .description("desc")
                .releaseDate(LocalDate.of(1800, Month.DECEMBER, 1))
                .duration(50)
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(f))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'releaseDate' must be after 28-DEC-1895]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_FilmsDuration_isNegative_andStatusIs400() throws Exception {
        Film f = Film.builder()
                .name("Test film")
                .description("desc")
                .releaseDate(LocalDate.now())
                .duration(-50)
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'duration' duration can not be negative]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void when_FilmsId_isNegative_andStatusIs400() throws Exception {
        Film f = Film.builder()
                .id(-1)
                .name("Test film")
                .description("test desc")
                .releaseDate(LocalDate.now())
                .duration(50)
                .build();

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertEquals("[field 'id' must be positive]",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void films_ListIsNotEmpty_andStatusIs200() throws Exception {
        Film f = Film.builder()
                .id(1)
                .name("New film upd")
                .description("Desc of new film")
                .releaseDate(LocalDate.now())
                .duration(25)
                .build();

        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(f))));

    }

}
