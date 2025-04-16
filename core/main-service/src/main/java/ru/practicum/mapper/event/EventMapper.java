package ru.practicum.mapper.event;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

import ru.practicum.mapper.location.LocationMapper;
import ru.practicum.mapper.user.UserShortMapper;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * The interface Event mapper.
 */
@Mapper(componentModel = "spring", uses = {UserShortMapper.class, LocationMapper.class})
public interface EventMapper {

    /**
     * To event event.
     *
     * @param eventFullDto the event full dto
     * @return the event
     */
    Event toEvent(EventFullDto eventFullDto);

    /**
     * To event short dto event short dto.
     *
     * @param event the event
     * @return the event short dto
     */
    EventShortDto toEventShortDto(Event event);

    /**
     * Format date time string.
     *
     * @param dateTime the date time
     * @return the string
     */
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
