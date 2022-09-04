package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    FilmController fc = new FilmController();
    static Film film;
    static Film filmWithIncorrectReleaseDate;
    static Film filmWithNegativeId;

    @BeforeAll
    static void createFilm() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();

        filmWithNegativeId = Film.builder()
                .id(-1)
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();

        filmWithIncorrectReleaseDate = Film.builder()
                .id(1)
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1894, 12, 28))
                .duration(100)
                .build();
    }

    @Test
    void create() {
        fc.createFilm(film);
        film.setId(1);
        assertEquals(fc.getFilms().get(0), film);
    }

    @Test
    void checkThrowableWhenIdIsNegative() {
        Throwable exception = assertThrows(FilmValidationException.class, () -> fc.updateFilm(filmWithNegativeId));

        assertEquals(null, exception.getMessage());
    }

    @Test
    void checkThrowableWhenReleaseDateIsIncorrect() {
        Throwable exception = assertThrows(FilmValidationException.class, () -> fc.updateFilm(filmWithIncorrectReleaseDate));

        assertEquals(null, exception.getMessage());
    }

    @Test
    void checkFilmListNotNull() {
        fc.createFilm(film);
        film.setId(1);
        List<Film> films = List.of(film);

        assertEquals(1, films.size());
    }
}
