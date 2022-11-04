package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    void testFindById() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void testFindAllFilms() {
        Optional<List<Film>> filmList = Optional.ofNullable(filmStorage.getAll());

        assertThat(filmList)
                .isPresent();

        assertEquals(3, filmList.get().size());
    }

    @Test
    void test_filmNotFound() {
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> filmStorage.findById(16));
        String message = "film with id 16 not found";

        assertThat(nfe.getMessage())
                .isEqualTo(message);
    }

    @Test
    void test_createFilm() {
        Film film = new Film();
        film.setName("test film creating");
        film.setDescription("desc for test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(180);
        film.getMpa().setId(3);

        filmStorage.create(film);
        film.setId(4);
        film.getMpa().setName("PG-13");
        film.setRate(0);

        assertThat(film)
                .isEqualTo(filmStorage.findById(4));
    }

    @Test
    void test_updateFilm() {
        Optional<Film> filmToUpdate = Optional.ofNullable(filmStorage.findById(2));

        assertThat(filmToUpdate)
                .isPresent();

        filmStorage.update(filmToUpdate.get());

        assertThat(filmToUpdate)
                .contains(filmStorage.findById(2));
    }

    @Test
    void test_addLike() {
        filmStorage.addLike(1, 2);

        assertThat(filmStorage.findById(1))
                .hasFieldOrPropertyWithValue("likes", Set.of(2));
    }

    @Test
    void test_deleteLike() {
        filmStorage.addLike(1, 2);

        assertThat(filmStorage.findById(1))
                .hasFieldOrPropertyWithValue("likes", Set.of(2));

        filmStorage.deleteLike(1, 2);
        assertThat(filmStorage.findById(1))
                .hasFieldOrPropertyWithValue("likes", Set.of());
    }

    @Test
    void test_getFilmsTopByLikes() {
        filmStorage.addLike(2, 2);

        assertThat(filmStorage.getFilmsTop(2))
                .isNotEmpty()
                .isEqualTo(List.of(filmStorage.findById(2), filmStorage.findById(1)));
    }
}
