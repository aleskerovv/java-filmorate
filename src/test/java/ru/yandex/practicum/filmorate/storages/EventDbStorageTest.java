package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"file:src/test/resources/test-schema.sql",
        "file:src/test/resources/test-data-users-films.sql"})
public class EventDbStorageTest {

    private final EventService eventService;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewService reviewService;

    @Test
    void shouldReturnEmptyFeedWhenNoEvents() {
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(0, events.size(), "Feed not empty");
    }

    @Test
    void shouldReturnFeedWithTwoEventsWhenFriendAddedThenRemoved() {
        userService.addFriend(1, 2);
        userService.deleteFriend(1, 2);
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(2, events.size(), "Feed size incorrect");
        assertEquals(2, events.get(0).getEntityId());
        assertEquals(2, events.get(1).getEntityId());
        assertEquals(Event.EventType.FRIEND, events.get(0).getEventType());
        assertEquals(Event.EventType.FRIEND, events.get(1).getEventType());
        assertEquals(Event.Operation.ADD, events.get(0).getOperation());
        assertEquals(Event.Operation.REMOVE, events.get(1).getOperation());
    }

    @Test
    void shouldReturnFeedWithTwoEventsWhenFilmLikedThenRemoved() {
        filmService.addLike(1, 3);
        filmService.deleteLike(1, 3);
        List<Event> events = eventService.getFeedByUserId(3);
        assertEquals(2, events.size(), "Feed size incorrect");
        assertEquals(1, events.get(0).getEntityId());
        assertEquals(1, events.get(1).getEntityId());
        assertEquals(Event.EventType.LIKE, events.get(0).getEventType());
        assertEquals(Event.EventType.LIKE, events.get(1).getEventType());
        assertEquals(Event.Operation.ADD, events.get(0).getOperation());
        assertEquals(Event.Operation.REMOVE, events.get(1).getOperation());
    }

    @Test
    void shouldReturnEmptyFeedWhenAddFriendFails() {
        try {
            userService.addFriend(1, -1);
        } catch (Exception e) {

        }
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(0, events.size(), "Feed not empty");
    }

    @Test
    void shouldReturnEmptyFeedWhenRemoveFriendFails() {
        try {
            userService.deleteFriend(1, -1);
        } catch (Exception e) {

        }
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(0, events.size(), "Feed not empty");
    }

    @Test
    void shouldReturnEmptyFeedWhenRemoveLikeFails() {
        try {
            filmService.deleteLike(-1, 1);
        } catch (Exception e) {

        }
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(0, events.size(), "Feed not empty");
    }

    @Test
    void shouldReturnEmptyFeedWhenAddLikeFails() {
        try {
            filmService.addLike(-1, 1);
        } catch (Exception e) {

        }
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(0, events.size(), "Feed not empty");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIdIncorrect() {
        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> eventService.getFeedByUserId(-1));
        assertEquals("user with id -1 not found", e.getMessage());
    }

    @Test
    void shouldReturnFeedWithOneCreatedReview() {
        reviewService.createReview(
                new Review().setUserId(1)
                        .setFilmId(1)
                        .setUseful(1)
                        .setContent("User 1 review for film 1")
                        .setIsPositive(true));
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(1, events.size(), "Feed size incorrect");
        Event event = events.get(0);
        assertEquals(Event.EventType.REVIEW, event.getEventType(), "Wrong event type");
        assertEquals(Event.Operation.ADD, event.getOperation(), "Wrong operation type");
    }

    @Test
    void shouldReturnFeedWithOneDeletedReview() {
        reviewService.deleteReviewById(1);
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(1, events.size(), "Feed size incorrect");
        Event event = events.get(0);
        assertEquals(Event.EventType.REVIEW, event.getEventType(), "Wrong event type");
        assertEquals(Event.Operation.REMOVE, event.getOperation(), "Wrong operation type");
    }

    @Test
    void shouldReturnFeedWithOneUpdatedReview() {
        reviewService.updateReview(
                new Review().setUserId(1)
                        .setFilmId(1)
                        .setUseful(1)
                        .setContent("User 1 review for film 1")
                        .setIsPositive(true)
                        .setReviewId(1));
        List<Event> events = eventService.getFeedByUserId(1);
        assertEquals(1, events.size(), "Feed size incorrect");
        Event event = events.get(0);
        assertEquals(Event.EventType.REVIEW, event.getEventType(), "Wrong event type");
        assertEquals(Event.Operation.UPDATE, event.getOperation(), "Wrong operation type");
    }
}
