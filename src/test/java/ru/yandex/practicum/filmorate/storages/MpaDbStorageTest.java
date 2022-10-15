package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.MpaCategory;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/resources/test.sql"})
class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    void test_findById() {
        Optional<MpaCategory> mpaOptional = Optional.ofNullable(mpaStorage.getById(2));

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa)
                        .returns("PG", MpaCategory::getName)
                        .returns(2, MpaCategory::getId));
    }

    @Test
    void test_findAllMpaCategories() {
        Optional<List<MpaCategory>> mpaCategories = Optional.ofNullable(mpaStorage.getAll());

        assertThat(mpaCategories)
                .isPresent();

        assertThat(mpaCategories.get())
                .hasSize(5);
    }
}
