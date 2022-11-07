package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Event {
    private int eventId;
    private final int userId;
    private final int entityId;
    private final EventType eventType;
    private final Operation operation;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private final Date timestamp;

    public enum EventType{
        LIKE, REVIEW, FRIEND
    }

    public enum Operation{
        REMOVE, ADD, UPDATE
    }
}
