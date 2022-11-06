package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventDbStorage eventDbStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventService(@Qualifier("userDbStorage") UserStorage userStorage, EventDbStorage eventDbStorage) {
        this.userStorage = userStorage;
        this.eventDbStorage = eventDbStorage;
    }

    public List<Event> getFeedByUserId(int id) {
        //Try to find user by id to check if user exists. If not - NotFoundException is thrown
        userStorage.findById(id);
        return eventDbStorage.getFeedByUserId(id);
    }

    public void addNewEvent(int userId, int entityId, Event.EventType eventType,
                            Event.Operation operation, String tableName) {
        Event newEvent = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(LocalDateTime.now())
                .build();
        eventDbStorage.addNewEvent(newEvent, tableName);
    }
}
