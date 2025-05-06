package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.AnalyzerClient;
import ru.practicum.CollectorClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.exception.IncorrectValueException;
import ru.practicum.service.event.EventSearchParams;
import ru.practicum.service.event.EventService;
import ru.practicum.service.event.PublicSearchParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final CollectorClient collectorClient;

    private final AnalyzerClient analyzerClient;

    private final EventService eventService;
    private static final String X_EWM_USER_ID_HEADER = "X-EWM-USER-ID";

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

    @GetMapping("/{event-id}")
    public EventFullDto getEventById(@PathVariable("event-id") Long eventId, @RequestHeader(X_EWM_USER_ID_HEADER) long userId) {
        log.info("Получение информации о событии с id={}", eventId);

        // Получение события через сервис
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping("/recommendations")
    public List<EventRecommendationDto> getRecommendations(@RequestHeader(X_EWM_USER_ID_HEADER) long userId,
                                                           @RequestParam(defaultValue = "10") int maxResults) {
        var recommendationStream = analyzerClient.getRecommendationsForUser(userId, maxResults);
        var recommendationList = recommendationStream.toList();

        List<EventRecommendationDto> result = new ArrayList<>();
        for (RecommendationsMessages.RecommendedEventProto requestProto : recommendationList) {
            result.add(new EventRecommendationDto(requestProto.getEventId(), requestProto.getScore()));
        }
        return result;
    }

    @PutMapping("/{event-id}/like")
    public void likeEvent(@PathVariable("event-id") Long eventId,
                          @RequestHeader(X_EWM_USER_ID_HEADER) long userId) {
        eventService.addLike(userId, eventId);

        collectorClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }
}
