package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getFeedByUserId(int id);

    Event addNewEvent(Event event, String tableName);
}
