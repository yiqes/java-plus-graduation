package ru.practicum.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

/**
 * The type Event short dto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    String eventDate;
    Long id;
    UserDto initiator;
    Boolean paid;
    String title;
    Long views;



}
