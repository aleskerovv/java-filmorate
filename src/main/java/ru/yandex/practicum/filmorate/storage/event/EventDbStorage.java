package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Event> getFeedByUserId(int id) {
        isUserExists(id);
        String query = "SELECT event_id, user_id, event_id, user_id, timestamp, event_type," +
                " operation, entity_id FROM events WHERE user_id = ?";
        try {
            return jdbcTemplate.query(query, EventMapper::mapToFeed, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("id", String.format("user with id %d not found", id));
        }
    }

    public Event addNewEvent(Event event, String tableName) {
        String createQuery = "insert into events(user_id, event_type, operation, entity_id, entity_table_name, timestamp) " +
                "values (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createQuery, new String[]{"event_id"});
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setString(3, event.getOperation().name());
            stmt.setInt(4, event.getEntityId());
            stmt.setString(5, tableName);
            stmt.setTimestamp(6, event.getTimestamp());
            return stmt;
        }, keyHolder);

        Optional<Integer> id = Optional.of(keyHolder.getKey().intValue());
        event.setEventId(id.get());

        return event;
    }

    private void isUserExists(int id) {
        String sqlQuery = "select count(*) from users where id = ?";
        int result = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        if (result != 1) {
            throw new NotFoundException("id", String
                    .format("user with id %d does not exists", id));
        }
    }
}

