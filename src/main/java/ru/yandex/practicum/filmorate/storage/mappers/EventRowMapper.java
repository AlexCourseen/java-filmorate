package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getLong("event_id"));
        event.setTimestamp(resultSet.getTimestamp("timestamp"));
        event.setUserId(resultSet.getLong("user_id"));
        String eventTypeStr = resultSet.getString("event_type");
        if (eventTypeStr != null) {
            event.setEventType(EventType.valueOf(eventTypeStr));
        }
        String operationStr = resultSet.getString("operation");
        if (operationStr != null) {
            event.setOperation(Operation.valueOf(operationStr));
        }
        event.setEntityId(resultSet.getLong("entity_id"));

        return event;
    }
}
