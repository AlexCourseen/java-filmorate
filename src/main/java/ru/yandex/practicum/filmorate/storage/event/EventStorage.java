package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {
    Collection<Event> getAllEvents(long userId);

    Event getEvent(long id);

    void addEvent(long userId, EventType eventType, Operation operation, long entityId);

}
