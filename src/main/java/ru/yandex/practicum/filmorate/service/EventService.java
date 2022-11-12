package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventService(@Qualifier("userDbStorage") UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public List<Event> getFeedByUserId(int id) {
        //Try to find user by id to check if user exists. If not - NotFoundException is thrown
        userStorage.findById(id);
        return eventStorage.getFeedByUserId(id);
    }

    public void addNewEvent(int userId, int entityId, Event.EventType eventType,
                            Event.Operation operation, String tableName) {
        Event newEvent = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Date.from(Instant.now()))
                .build();
        eventStorage.addNewEvent(newEvent, tableName);
    }
}
