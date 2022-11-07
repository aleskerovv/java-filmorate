package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private static final String TABLE_NAME = "reviews";
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         ReviewStorage reviewStorage,
                         EventService eventService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.reviewStorage = reviewStorage;
        this.eventService = eventService;
    }

    public Review getReviewById(Integer id) {
        return reviewStorage.findById(id);
    }

    public Review createReview(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
        eventService.addNewEvent(review.getUserId(), review.getFilmId(),
                Event.EventType.REVIEW, Event.Operation.ADD, TABLE_NAME);

        return reviewStorage.create(review);
    }

    public Review updateReview(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
        review = reviewStorage.update(review);
        eventService.addNewEvent(review.getUserId(), review.getFilmId(),
                Event.EventType.REVIEW, Event.Operation.UPDATE, TABLE_NAME);
        return review;
    }

    public void deleteReviewById(Integer id) {
        Review review = getReviewById(id);
        eventService.addNewEvent(review.getUserId(), review.getFilmId(),
                Event.EventType.REVIEW, Event.Operation.REMOVE, TABLE_NAME);
        reviewStorage.deleteById(id);
    }

    public List<Review> getReviewsByParameters(Integer filmId, int count) {
        if (filmId != null) {
            filmStorage.findById(filmId);
        }

        return reviewStorage.getReviewsByParameters(filmId, count);
    }

    public void addLike(Integer reviewId, Integer userId) {
        userStorage.findById(userId);

        reviewStorage.addLike(reviewId, userId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        userStorage.findById(userId);

        reviewStorage.removeLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        userStorage.findById(userId);

        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        userStorage.findById(userId);

        reviewStorage.removeDislike(reviewId, userId);
    }
}
