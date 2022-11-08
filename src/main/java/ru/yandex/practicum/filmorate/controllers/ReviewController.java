package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Operations with Reviews")
public class ReviewController {
    ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "returns review by id if exists")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @PostMapping
    @Operation(summary = "creates new review")
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    @Operation(summary = "updates review if exists")
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "deletes review if exists")
    public void deleteReviewById(@PathVariable Integer id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping
    @Operation(summary = "returns review by optional parameters:count or filmId or both")
    public List<Review> getReviewsByParameters(@RequestParam(value = "count",defaultValue = "10", required = false)
                                              Integer count,
                                          @RequestParam(value = "filmId", required = false) Integer filmId) {
        return reviewService.getReviewsByParameters(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @Operation(summary = "adds like to review from user")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Operation(summary = "removes like to review from user if exists")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @Operation(summary = "adds dislike to review from users")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @Operation(summary = "deletes dislike to review from user if exists")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.removeDislike(id, userId);
    }
}
