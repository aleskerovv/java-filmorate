package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        "file:src/test/resources/test-data-users-films.sql",
        "file:src/test/resources/test-data-directors.sql"})
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

        assertThat(filmStorage.getFilmsTop(2,-1,-1))
                .isNotEmpty()
                .isEqualTo(List.of(filmStorage.findById(2), filmStorage.findById(1)));
    }

    @Test
    void test_getFilmsTopByLikesWithGenre() {
        assertThat(filmStorage.getFilmsTop(2,1,-1))
                .isNotEmpty()
                .isEqualTo(List.of(filmStorage.findById(1)));
    }

    @Test
    void test_getFilmsTopByLikesWithYear() {
        assertThat(filmStorage.getFilmsTop(2,-1,2020))
                .isNotEmpty()
                .isEqualTo(List.of(filmStorage.findById(3)));
    }

    @Test
    void test_getFilmsTopByLikesWithGenreAndYear() {
        assertThat(filmStorage.getFilmsTop(2,2,2021))
                .isNotEmpty()
                .isEqualTo(List.of(filmStorage.findById(2)));
    }

    @Test
    void test_deleteFilm() {
        filmStorage.deleteById(1);
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> filmStorage.findById(1));
        String message = "film with id 1 not found";

        assertThat(nfe.getMessage())
                .isEqualTo(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {"film", "Film", "IL"})
    @DisplayName("Check that film was found case-insensitive")
    void test_searchFilmByTitle(String filter) {
        assertThat(filmStorage.searchFilm(filter, List.of("title")).size())
                .isEqualTo(3);
    }

    @Test
    @DisplayName("Check that film was found in correct order")
    void test_searchFilmByTitleWithOrder() {
        filmStorage.addLike(2, 2);
        List<Film> found = filmStorage.searchFilm("film", List.of("title"));

        assertEquals(3, found.size());
        assertThat(found.get(0))
                .hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @DisplayName("Check that film was not found")
    void test_searchFilmByTitleWithNotExistName() {

        assertThat(filmStorage.searchFilm("qwerty123", List.of("title")))
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"EN", "En", "en"})
    @DisplayName("Check that film was found case-insensitive")
    void test_searchFilmByDirector(String filter) {
        assertThat(filmStorage.searchFilm(filter, List.of("director")).size())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Check that film was found in correct order")
    void test_searchFilmByDirectorWithOrder() {
        filmStorage.addLike(1, 2);
        List<Film> found = filmStorage.searchFilm("en", List.of("director"));

        assertEquals(2, found.size());
        assertThat(found.get(0))
                .hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @DisplayName("Check that film was not found")
    void test_searchFilmByDirectorWithNotExistName() {

        assertThat(filmStorage.searchFilm("qwerty123", List.of("director")))
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ST", "St", "st"})
    @DisplayName("Check that film was found case-insensitive")
    void test_searchFilmByTitleAndDirector(String filter) {
        assertThat(filmStorage.searchFilm(filter, List.of("director", "title")).size())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Check that common film was found")
    void test_getCommonFilms() {
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(1, 3);
        filmStorage.addLike(3, 1);

        List<Film> found = filmStorage.getCommonFilms(1, 2);

        assertEquals(2, found.size());
        assertThat(found.get(0))
                .hasFieldOrPropertyWithValue("rate", 3);
    }

    @Test
    @DisplayName("Check getFilmsByDirectorSortByYear")
    void test_getFilmsByDirectorSortByYear() {
        List<Film> found = filmStorage.getFilmsByDirector(2, "year");
        assertEquals(2, found.size());
        assertThat(found.get(0))
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 2));
    }

    @Test
    @DisplayName("Check getFilmsByDirectorSortByLikes")
    void test_getFilmsByDirectorSortByLikes() {
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 3);
        filmStorage.addLike(3, 1);
        List<Film> found = filmStorage.getFilmsByDirector(2, "likes");
        assertEquals(2, found.size());
        assertThat(found.get(0))
                .hasFieldOrPropertyWithValue("rate", 3);
        assertThat(found.get(1))
                .hasFieldOrPropertyWithValue("rate", 1);
    }
}
