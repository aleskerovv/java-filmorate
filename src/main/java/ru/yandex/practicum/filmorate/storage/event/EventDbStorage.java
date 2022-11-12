package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUserId(int id) {
        String query = "SELECT event_id, user_id, event_timestamp, event_type," +
                " operation, entity_id FROM events WHERE user_id = ?" +
                "ORDER BY event_id";
        return jdbcTemplate.query(query, EventMapper::mapToFeed, id);
    }

    @Override
    public Event addNewEvent(Event event, String tableName) {
        String createQuery = "INSERT INTO events(user_id, event_type, operation, entity_id, entity_table_name," +
                " event_timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createQuery, new String[]{"event_id"});
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setString(3, event.getOperation().name());
            stmt.setInt(4, event.getEntityId());
            stmt.setString(5, tableName);
            stmt.setTimestamp(6, new Timestamp(event.getTimestamp().getTime()));
            return stmt;
        }, keyHolder);
        event.setEventId(keyHolder.getKey().intValue());
        return event;
    }
}

