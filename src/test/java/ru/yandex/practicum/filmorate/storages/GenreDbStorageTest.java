package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void test_findById() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getById(1));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre)
                        .returns("Комедия", Genre::getName)
                        .returns(1, Genre::getId));
    }

    @Test
    void test_findAllGenres() {
        Optional<List<Genre>> genresList = Optional.ofNullable(genreStorage.getAll());

        assertThat(genresList)
                .isPresent();

        assertThat(genresList.get())
                .hasSize(6);
    }
}
