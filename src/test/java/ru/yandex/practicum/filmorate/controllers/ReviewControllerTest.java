package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.filmorate.model.Review;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_newReview_andStatusIs200() throws Exception {
        Review review = new Review();
        review.setContent("Test review")
                .setIsPositive(true)
                .setFilmId(1)
                .setUserId(1);

        mockMvc.perform(
                post("/reviews")
                        .content(objectMapper.writeValueAsString(review))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));
    }

    @Test
    void when_Review_filmIdIsEmpty_statusIs400() throws Exception {
        Review review = new Review();
        review.setContent("test")
                .setIsPositive(false)
                .setUserId(1);

        mockMvc.perform(
                        post("/reviews")
                                .content(objectMapper.writeValueAsString(review))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.filmId")
                        .value("filmId can not be null"));
    }

    @Test
    void when_Review_userIdIsEmpty_statusIs400() throws Exception {
        Review review = new Review();
        review.setContent("test")
                .setIsPositive(false)
                .setFilmId(1);

        mockMvc.perform(
                        post("/reviews")
                                .content(objectMapper.writeValueAsString(review))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.userId")
                        .value("userId can not be null"));
    }

    @Test
    void when_Review_isPositiveIsEmpty_statusIs400() throws Exception {
        Review review = new Review();
        review.setContent("test")
                .setFilmId(1)
                .setUserId(1);

        mockMvc.perform(
                        post("/reviews")
                                .content(objectMapper.writeValueAsString(review))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.isPositive")
                        .value("isPositive can not be null"));
    }

    @Test
    void test_findReview_byId() throws Exception {
        Review review = new Review();
        review.setContent("positive review for film 1 from user 1")
                .setIsPositive(true)
                .setUserId(1)
                .setFilmId(1)
                .setUseful(0)
                .setReviewId(1);
        mockMvc.perform(
                        get("/reviews/1")

                ).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

    @Test
    void test_findReviews_withCount1() throws Exception {
        mockMvc.perform(
                        get("/reviews?count=1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$..reviewId").value(1));
    }

    @Test
    void test_findReviews_withFilmId1() throws Exception {
        mockMvc.perform(
                        get("/reviews?filmId=1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$..reviewId").value(1));
    }

    @Test
    void test_addLike_toReview() throws Exception {
        mockMvc.perform(
                put("/reviews/1/like/2")
        ).andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(1));
    }

    @Test
    void test_addDislike_toReview() throws Exception {
        mockMvc.perform(
                put("/reviews/1/dislike/2")
        ).andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(-1));
    }

    @Test
    void test_removeLike_fromReview() throws Exception {
        mockMvc.perform(
                put("/reviews/1/like/2")
        ).andExpect(status().isOk());

        mockMvc.perform(
                delete("/reviews/1/like/2")
        ).andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    void test_removeDislike_fromReview() throws Exception {
        mockMvc.perform(
                put("/reviews/1/dislike/2")
        ).andExpect(status().isOk());

        mockMvc.perform(
                delete("/reviews/1/dislike/2")
        ).andExpect(status().isOk());

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    void when_addingLikeToReview_whichNotPresent() throws Exception {
        mockMvc.perform(
                        put("/reviews/5/like/2")
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof NotFoundException))
                .andExpect(result -> assertEquals("{\"id\":\"review with id 5 does not exists\"}", result.getResponse().getContentAsString()));
    }
}
