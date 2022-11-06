package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EntityStorage;

import java.util.List;

public interface ReviewStorage extends EntityStorage<Review> {
    List<Review> getReviewsByParameters(Integer filmId, int count);

    void addLike(Integer reviewId, Integer userId);

    void removeLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void removeDislike(Integer reviewId, Integer userId);
}
