package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private long eventId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp timestamp;
    private EventType eventType;
    private Operation operation;
    private long userId;
    private long entityId;

    public void setCurrentTimestamp() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
