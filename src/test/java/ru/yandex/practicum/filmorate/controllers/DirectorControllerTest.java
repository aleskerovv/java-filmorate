package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data-users-films.sql"})
class DirectorControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private Director getValidateDirector(String name) {
        Director director = new Director();
        director.setId(0);
        director.setName(name);
        return director;
    }

    @Test
    @DisplayName("Create Director")
    void EqualsWhenDirectorCreate() throws Exception {
        Director director = getValidateDirector("Steven Spielberg");
        mockMvc.perform(post("/directors")
                .content(objectMapper.writeValueAsString(director))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Steven Spielberg"));
    }

    @Test
    @DisplayName("Create Director when name is null")
    void NotValidWhenNameIsNull() throws Exception {
        Director director = getValidateDirector("Steven Spielberg");
        director.setName(null);
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.name").value("can not be blank"));
    }

    @Test
    @DisplayName("Create Director when name is blank")
    void NotValidWhenNameIsBlank() throws Exception {
        Director director = getValidateDirector("");
        mockMvc.perform(post("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.name").value("can not be blank"));
    }

    @Test
    @DisplayName("Find director by id")
    void EqualsWhenGetById() throws Exception {
        Director director = getValidateDirector("Steven Spielberg");
        String bodyContent = mockMvc.perform(post("/directors")
                .content(objectMapper.writeValueAsString(director))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andReturn().getResponse().getContentAsString();
        Director director_response = objectMapper.readValue(bodyContent, Director.class);
        int id = director_response.getId();
        mockMvc.perform(get("/directors/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Steven Spielberg"));
    }

    @Test
    @DisplayName("Find director by wrong id")
    void NotValidWhenIdNotExist() throws Exception {
        mockMvc.perform(get("/directors/1"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Get all directors")
    void EqualsWhenGetAll() throws Exception {
        Director director1 = getValidateDirector("Steven Spielberg");
        Director director2 = getValidateDirector("Quentin Tarantino");
        mockMvc.perform(post("/directors")
                .content(objectMapper.writeValueAsString(director1))
                .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(post("/directors")
                .content(objectMapper.writeValueAsString(director2))
                .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(get("/directors"))
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    @DisplayName("Update director with wrong id")
    void NotValidUpdateWhenDirectorNotExist() throws Exception {
        Director director = getValidateDirector("Steven Spielberg");
        director.setId(1);
        mockMvc.perform(put("/directors")
                .content(objectMapper.writeValueAsString(director))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Delete director with wrong id")
    void RightCodeWhenDeleteWithWrongId() throws Exception {
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete director")
    void RightCodeWhenDelete() throws Exception {
        Director director = getValidateDirector("Steven Spielberg");
        mockMvc.perform(post("/directors")
                .content(objectMapper.writeValueAsString(director))
                .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isOk());
    }
}