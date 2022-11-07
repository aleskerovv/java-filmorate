package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicateEventException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String DISLIKE = "DISLIKE";
    private static final String LIKE = "LIKE";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getReviewsByParameters(Integer filmId, int count) {
        List<Review> reviews;
        if (filmId != null) {
            String query = "SELECT * FROM reviews " +
                    "WHERE film_id = ? " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            reviews = jdbcTemplate.query(query, ReviewMapper::mapToReview, filmId, count);
        } else {
            String query = "SELECT * FROM reviews " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            reviews = jdbcTemplate.query(query, ReviewMapper::mapToReview, count);
        }

        return reviews;
    }

    @Override
    public Review findById(Integer id) {
        try {
            String reviewQuery = "SELECT * FROM reviews " +
                    "WHERE review_id = ?";

            return jdbcTemplate.queryForObject(reviewQuery, ReviewMapper::mapToReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("review with id %d does not exists", id));
        }
    }

    @Override
    public Review create(Review review) {
        String query = "INSERT INTO reviews(content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, kh);

        Optional<Integer> id = Optional.of(kh.getKey().intValue());
        review.setReviewId(id.get());

        log.info("created review with id {}", id.get());

        return review;
    }

    @Override
    public Review update(Review review) {
        this.isReviewExists(review.getReviewId());

        String query = "UPDATE reviews set " +
                "content = ?, is_positive = ? " +
                "WHERE review_id = ?";

        jdbcTemplate.update(query, review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.info("updated review with id {}", review.getReviewId());
        //Необходимо запрашивать обновленный из базы, т.к. в присланном теле запроса может быть неверный фильм/юзер
        return findById(review.getReviewId());
    }

    @Override
    public void deleteById(Integer id) {
        String query = "DELETE FROM reviews " +
                "WHERE review_id = ?";

        jdbcTemplate.update(query, id);

        log.info("deleted review with id {}", id);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        this.isReviewExists(reviewId);

        String checkQuery =
                "SELECT review_id " +
                        "FROM reviews_rates " +
                        "WHERE user_id = ? " +
                        "AND review_id = ?;";

        if (!jdbcTemplate
                .query(checkQuery, (rs, n) -> rs.getInt("review_id"), userId, reviewId)
                .isEmpty()) {
            throw new DuplicateEventException(String.format("Like on review with id %d from user with id" +
                            " %s already exist",
                    reviewId, userId));
        }

        String query = "merge into reviews_rates(review_id, user_id, rate) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(query, reviewId, userId, LIKE);

        this.increaseReviewRate(reviewId);

        log.info("user with id {} liked review with id {}", userId, reviewId);
    }

    @Override
    public void removeLike(Integer reviewId, Integer userId) {
        this.isReviewExists(reviewId);

        String checkQuery = "SELECT review_id " +
                        "FROM reviews_rates " +
                        "WHERE user_id = ? " +
                        "AND review_id = ?;";

        if (jdbcTemplate
                .query(checkQuery, (rs, n) -> rs.getInt("review_id"), userId, reviewId)
                .isEmpty()) {
            throw new NotFoundException("review", String.format("Like on review with id %d from user with id" +
                            " %s not found",
                    reviewId, userId));
        }

        String query = "delete from reviews_rates " +
                "where review_id = ? and user_id = ? and rate = ?";
        jdbcTemplate.update(query, reviewId, userId, LIKE);

        this.decreaseReviewRate(reviewId);

        log.info("user with id {} removed like from review with id {}", userId, reviewId);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        this.isReviewExists(reviewId);

        String checkQuery = "SELECT review_id " +
                        "FROM reviews_rates " +
                        "WHERE user_id = ? " +
                        "AND review_id = ?;";

        if (!jdbcTemplate
                .query(checkQuery, (rs, n) -> rs.getInt("review_id"), userId, reviewId)
                .isEmpty()) {
            throw new DuplicateEventException(String.format("Dislike on review with id %d from user with id" +
                            " %s already exist",
                    reviewId, userId));
        }

        String query = "merge into reviews_rates(review_id, user_id, rate) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(query, reviewId, userId, DISLIKE);

        this.decreaseReviewRate(reviewId);

        log.info("user with id {} disliked review with id {}", userId, reviewId);
    }

    @Override
    public void removeDislike(Integer reviewId, Integer userId) {
        this.isReviewExists(reviewId);

        String checkQuery = "SELECT review_id " +
                        "FROM reviews_rates " +
                        "WHERE user_id = ? " +
                        "AND review_id = ?;";

        if (jdbcTemplate
                .query(checkQuery, (rs, n) -> rs.getInt("review_id"), userId, reviewId)
                .isEmpty()) {
            throw new NotFoundException("review", String.format("Dislike on review with id %d from user with id" +
                            " %s not found",
                    reviewId, userId));
        }

        String query = "delete from reviews_rates " +
                "where review_id = ? and user_id = ? and rate = ?";
        jdbcTemplate.update(query, reviewId, userId, DISLIKE);

        this.increaseReviewRate(reviewId);

        log.info("user with id {} removed dislike from review with id {}", userId, reviewId);
    }

    private void increaseReviewRate(Integer id) {
        String updateReviewUseful = "UPDATE reviews " +
                "SET useful = useful + 1 " +
                "WHERE review_id = ?";

        jdbcTemplate.update(updateReviewUseful, id);
    }

    private void decreaseReviewRate(Integer id) {
        String updateReviewUseful = "UPDATE reviews " +
                "SET useful = useful - 1 " +
                "WHERE review_id = ?";

        jdbcTemplate.update(updateReviewUseful, id);
    }

    private void isReviewExists(Integer id) {
        String sqlQuery = "select count(*) from reviews where review_id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (result != 1) {
            throw new NotFoundException("id", String
                    .format("review with id %d does not exists", id));
        }
    }
}