package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class ReviewDbStorageTest {
    private final ReviewDbStorage reviewStorage;

    @Test
    void test_findReviewById() {
        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1));
    }

    @Test
    void test_findReviews_whereFilmIdIs1() {
        Optional<List<Review>> optionalReviews = Optional.ofNullable(reviewStorage.getReviewsByParameters(1,
                2));

        assertThat(optionalReviews)
                .isPresent()
                .isNotEmpty();

        assertEquals(1, optionalReviews.get().size());
    }

    @Test
    void test_findReviews_whereFilmIdNotStated() {
        Optional<List<Review>> optionalReviews = Optional.ofNullable(reviewStorage.getReviewsByParameters(null,
                10));

        assertThat(optionalReviews)
                .isPresent()
                .isNotEmpty();

        assertEquals(2, optionalReviews.get().size());
    }

    @Test
    void test_createNewReview() {
        Review review = new Review();

        review.setContent("Review for film 3")
                .setIsPositive(true)
                .setFilmId(3)
                .setUserId(1);

        reviewStorage.create(review);
        review.setReviewId(3);
        review.setUseful(0);

        assertEquals(review, reviewStorage.findById(3));
    }

    @Test
    void test_ReviewNotFound() {
        NotFoundException nfe = assertThrows(NotFoundException.class, () -> reviewStorage.findById(16));
        String message = "review with id 16 not found";

        Assertions.assertThat(nfe.getMessage())
                .isEqualTo(message);
    }

    @Test
    void test_UpdateReview() {
        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        reviewOptional.get().setIsPositive(false);

        reviewStorage.update(reviewOptional.get());

        Optional<Review> updatedReview = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(updatedReview)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("isPositive",
                                false));
    }

    @Test
    void test_addLike() {
        reviewStorage.addLike(1,2);
        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful",
                                1));
    }

    @Test
    void test_deleteLike() {
        reviewStorage.addLike(1,2);
        reviewStorage.removeLike(1,2);

        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful",
                                0));
    }

    @Test
    void test_addDislike() {
        reviewStorage.addDislike(1,2);
        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful",
                                -1));
    }

    @Test
    void test_deleteDislike() {
        reviewStorage.addDislike(1,2);
        reviewStorage.removeDislike(1,2);

        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.findById(1));

        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("useful",
                                0));
    }
}
