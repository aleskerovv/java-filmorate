package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Builder
public class Event {
    @Setter
    private int eventId;
    private final int userId;
    private final int entityId;
    private final EventType eventType;
    private final Operation operation;
    private final Timestamp timestamp;

    public enum EventType{
        LIKE, REVIEW, FRIEND
    }

    public enum Operation{
        REMOVE, ADD, UPDATE
    }
}
