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
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmStorage filmStorage;

    @BeforeEach
    void clear() {
        filmStorage.deleteAll();
    }

    @Test
    void creates_newFilm_andStatusIs200() throws Exception {
        Film f = new Film();
        f.setName("New film");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(50);


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
        Film f2 = new Film();
        f2.setId(1);
        f2.setName("New film upd");
        f2.setDescription("Desc of new film");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);

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
                        instanceof NotFoundException))
                .andExpect(jsonPath("$.id").value("must be positive"));
    }

    @Test
    void films_ListIsNotEmpty_andStatusIs200() throws Exception {
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(f))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

        f.setId(1);

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(f))));
    }

    @Test
    void addLikeToFilm_AndLikesCountIs1() throws Exception {
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);

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
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);

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

        Film f2 = new Film();
        f2.setName("New f2ilm upd");
        f2.setDescription("Desc of2 new f2ilm");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);

        Film f3 = new Film();
        f3.setName("New film upd");
        f3.setDescription("Desc of new film");
        f3.setReleaseDate(LocalDate.now());
        f3.setDuration(25);

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
        Film f = new Film();
        f.setName("New film upd");
        f.setDescription("Desc of new film");
        f.setReleaseDate(LocalDate.now());
        f.setDuration(25);

        Film f2 = new Film();
        f2.setName("New f2ilm upd");
        f2.setDescription("Desc of2 new f2ilm");
        f2.setReleaseDate(LocalDate.now());
        f2.setDuration(25);

        Film f3 = new Film();
        f3.setName("New film upd");
        f3.setDescription("Desc of new film");
        f3.setReleaseDate(LocalDate.now());
        f3.setDuration(25);

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

        mockMvc.perform(put("/films/3/like/1"))
                .andExpect(status().isOk());
        f3.getLikes().add(1);
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
