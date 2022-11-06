package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper {
    public static Review mapToReview(ResultSet rs, int rowNumber) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"))
                .setContent(rs.getString("content"))
                .setIsPositive(rs.getBoolean("is_positive"))
                .setUserId(rs.getInt("user_id"))
                .setFilmId(rs.getInt("film_id"))
                .setUseful(rs.getInt("useful"));

        return review;
    }
}
