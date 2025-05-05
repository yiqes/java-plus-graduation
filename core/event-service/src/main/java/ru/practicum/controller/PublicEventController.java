package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.IncorrectValueException;
import ru.practicum.service.event.EventSearchParams;
import ru.practicum.service.event.EventService;
import ru.practicum.service.event.PublicSearchParams;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constant.Constant.PATTERN_DATE;

/**
 * The type Public event controller.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    /**
     * Gets events.
     *
     * @param text          the text
     * @param categories    the categories
     * @param paid          the paid
     * @param rangeStart    the range start
     * @param rangeEnd      the range end
     * @param onlyAvailable the only available
     * @param from          the from
     * @param size          the size
     * @return the events
     */
    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN_DATE) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        log.info("==> GET /events Public searching events with params: " +
                        "text {}, categories: {}, paid {}, rangeStart: {}, rangeEnd: {}, available {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectValueException("rangeStart of event can't be after rangeEnd");
        }

        EventSearchParams eventSearchParams = new EventSearchParams();
        PublicSearchParams publicSearchParams = new PublicSearchParams();
        publicSearchParams.setText(text);
        publicSearchParams.setCategories(categories);
        publicSearchParams.setPaid(paid);

        publicSearchParams.setRangeStart(rangeStart);
        publicSearchParams.setRangeEnd(rangeEnd);

        eventSearchParams.setPublicSearchParams(publicSearchParams);
        eventSearchParams.setFrom(from);
        eventSearchParams.setSize(size);

        String clientIp = httpRequest.getRemoteAddr();

        List<EventShortDto> eventShortDtoList = eventService.getAllByPublic(eventSearchParams, onlyAvailable, sort, clientIp);
        log.info("<== GET /events Returning public searching events. List size: {}",
                eventShortDtoList.size());
        log.info("==> GET /events: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventShortDtoList;
    }

    /**
     * Gets event by id.
     *
     * @param eventId the event id
     * @param request the request
     * @return the event by id
     */
    @GetMapping("/{event-id}")
    public EventFullDto getEventById(@PathVariable("event-id") Long eventId, HttpServletRequest request) {
        log.info("Получение информации о событии с id={}", eventId);

        // Получение IP клиента
        String clientIp = request.getRemoteAddr();

        // Получение события через сервис
        return eventService.getEventById(eventId, clientIp);
    }
}
