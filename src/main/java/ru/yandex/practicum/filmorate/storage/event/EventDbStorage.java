package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;


@Repository("eventDbStorage")
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    private final UserStorage userStorage;

    private static final String FIND_ALL_EVENTS = "SELECT * FROM events " +
            "WHERE user_id = ? " +
            "ORDER BY timestamp ASC";

    private static final String FIND_EVENT_BY_ID = "SELECT * FROM events where event_id = ?";

    private static final String INSERT_QUERY = "INSERT INTO events (timestamp, user_id, event_type, operation, " +
            "entity_id) VALUES (?, ?, ?, ?, ?)";

    @Autowired
    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper, @Qualifier("userDbStorage") UserStorage userStorage) {
        super(jdbc, mapper);
        this.userStorage = userStorage;
    }

    @Override
    public Event getEvent(long id) {
        return findOne(FIND_EVENT_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найден"));
    }

    @Override
    public Collection<Event> getAllEvents(long userId) {
        userStorage.getUser(userId);
        return findMany(FIND_ALL_EVENTS, userId);
    }

    @Override
    public void addEvent(long userId, EventType eventType, Operation operation, long entityId) {
        userStorage.getUser(userId);
        Event event = new Event();
        event.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        save(event);
    }

    private Event save(Event event) {
        String eventType = event.getEventType() != null ? event.getEventType().name() : null;
        String operation = event.getOperation() != null ? event.getOperation().name() : null;

        Timestamp timestamp = event.getTimestamp();

        Object[] params = new Object[]{
                timestamp,
                event.getUserId(),
                eventType,
                operation,
                event.getEntityId()
        };
        long id = insert(INSERT_QUERY, params);
        return getEvent(id);
    }
}
