package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.feed.EventDbStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventDbStorage eventDbStorage;

    public List<Event> getFeedByUserId(int id) {
        return eventDbStorage.getFeedByUserId(id);
    }

    public void addNewEvent(int userId, int entityId, Event.EventType eventType,
                            Event.Operation operation, String tableName) {
        Event newEvent = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Timestamp.from(Instant.now()))
                .build();
        eventDbStorage.addNewEvent(newEvent, tableName);
    }
}
