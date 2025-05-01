package ru.practicum.mapper.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.enums.EventState;
import ru.practicum.mapper.category.CategoryMapper;
import ru.practicum.mapper.location.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.service.category.CategoryService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Util event class.
 */
@Component
@RequiredArgsConstructor
public class UtilEventClass {

    private final CategoryMapper categoryMapper;

    private final CategoryService categoryService;

    //private final UserMapper userMapper;

    //private final UserShortMapper userShortMapper;

    private final LocationMapper locationMapper;

    private final UserServiceClient userServiceClient;

    public Event toEventFromNewEventDto(NewEventDto newEventDto, Long userId, CategoryDto category, Location location) {
        if (newEventDto == null || userId == null || category == null || location == null) {
            return null;
        }


        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryMapper.toEntity(category));
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now().withNano(0));
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiatorId(userId);
        event.setLocation(location);
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setPublishedOn(null);
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setState(EventState.PENDING);
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    /**
     * Update event from dto.
     *
     * @param event                   the event
     * @param updateEventAdminRequest the update event admin request
     */
    public void updateEventFromDto(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest == null || event == null) {
            return;
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            CategoryDto categoryDto = categoryService.getCategoryById(updateEventAdminRequest.getCategory());
            event.setCategory(categoryMapper.toCategoryFromCategoryDto(categoryDto));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
    }

    /**
     * Update event event.
     *
     * @param updatedEvent the updated event
     * @param request      the request
     * @param newCategory  the new category
     * @param location     the location
     * @return the event
     */
    public Event updateEvent(Event updatedEvent, UpdateEventAdminRequest request, Category newCategory,
                             Location location) {
        return Event.builder()
                .id(updatedEvent.getId())
                .annotation(request.getAnnotation() != null ? request.getAnnotation() : updatedEvent.getAnnotation())
                .category(request.getCategory() != null ? newCategory : updatedEvent.getCategory())
                .confirmedRequests(updatedEvent.getConfirmedRequests())
                .createdOn(updatedEvent.getCreatedOn())
                .description(request.getDescription() != null ? request.getDescription() : updatedEvent.getDescription())
                .eventDate(request.getEventDate() != null ? request.getEventDate() : updatedEvent.getEventDate())
                .initiatorId(updatedEvent.getInitiatorId())
                .location(location != null ? location : updatedEvent.getLocation())
                .paid(request.getPaid() != null ? request.getPaid() : updatedEvent.getPaid())
                .participantLimit(request.getParticipantLimit() != null ?
                        request.getParticipantLimit() : updatedEvent.getParticipantLimit())
                .publishedOn(updatedEvent.getPublishedOn())
                .requestModeration(request.getRequestModeration() != null ?
                        request.getRequestModeration() : updatedEvent.getRequestModeration())
                .state(updatedEvent.getState())
                .title(request.getTitle() != null ? request.getTitle() : updatedEvent.getTitle())
                .views(updatedEvent.getViews())
                .build();
    }

    /**
     * To event full dto event full dto.
     *
     * @param event the event
     * @return the event full dto
     */
    public EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn().format(formatter));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(userServiceClient.getById(event.getInitiatorId()));
        eventFullDto.setLocation(locationMapper.toLocationDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(formatter) : null);
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());
        eventFullDto.setEventDate(event.getEventDate().format(formatter));

        return eventFullDto;
    }
}