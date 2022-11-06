package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Event {
    private int eventId;
    private final int userId;
    private final int entityId;
    private final EventType eventType;
    private final Operation operation;
    private final LocalDateTime timestamp;

    public enum EventType{
        LIKE, REVIEW, FRIEND
    }

    public enum Operation{
        REMOVE, ADD, UPDATE
    }
}
