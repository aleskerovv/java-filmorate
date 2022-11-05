package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql",
        "file:src/test/resources/test-data-directors.sql"})
class DirectorDbStorageTest {

    private final DirectorStorage directorStorage;

    @Test
    @DisplayName("Get directors by film id")
    void EqualsGetDirectorsByFilmId() {
        Optional<List<Director>> directorList = Optional.ofNullable(directorStorage.getDirectorsByFilmId(1));

        assertThat(directorList)
                .isPresent();

        assertEquals(2, directorList.get().size());
    }

    @Test
    @DisplayName("Get directors by film id")
    void iSEmptyWhenGetDirectorsByFilmId() {
        Optional<List<Director>> directorList = Optional.ofNullable(directorStorage.getDirectorsByFilmId(2));

        assertThat(directorList)
                .isPresent();

        assertEquals(0, directorList.get().size());
    }
}