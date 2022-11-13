package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
public class Event {
    @Setter
    private int eventId;
    private final int userId;
    private final int entityId;
    private final EventType eventType;
    private final Operation operation;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("timestamp")
    private final Date eventTime;

    public enum EventType{
        LIKE, REVIEW, FRIEND
    }

    public enum Operation{
        REMOVE, ADD, UPDATE
    }
}
