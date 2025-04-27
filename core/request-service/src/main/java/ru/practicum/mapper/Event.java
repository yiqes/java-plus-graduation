package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Long id;
    String annotation;
    Category category;
    LocalDateTime createdOn = LocalDateTime.now();
    String description;
    LocalDateTime eventDate;
    User initiator;
    Location location;
    boolean paid;
    int participantLimit;
    LocalDateTime publishedOn;
    boolean requestModeration;
    EventState state = EventState.PENDING;
    String title;
}
