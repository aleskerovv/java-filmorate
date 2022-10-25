package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(scripts = {"file:src/main/resources/schema.sql", "file:src/main/resources/data.sql"})
class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_findGenreById() throws Exception {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        mockMvc.perform(
                        get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(genre)));
    }

    @Test
    void test_findAllGenres() throws Exception {
        mockMvc.perform(
                        get("/genres")
                ).andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(status().isOk());
    }
}
