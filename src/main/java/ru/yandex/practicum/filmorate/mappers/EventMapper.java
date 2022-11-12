package ru.yandex.practicum.filmorate.mappers;

import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper {
    public static Event mapToFeed(ResultSet rs, int rowNumber) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .timestamp(rs.getTimestamp("event_timestamp"))
                .eventType(Event.EventType.valueOf(rs.getString("event_type")))
                .operation(Event.Operation.valueOf(rs.getString("operation")))
                .build();
    }
}
