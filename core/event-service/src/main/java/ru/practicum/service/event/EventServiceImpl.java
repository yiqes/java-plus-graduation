package ru.practicum.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.client.RequestServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.mapper.event.UtilEventClass;
import ru.practicum.mapper.location.LocationMapper;
import ru.practicum.model.*;
import ru.practicum.model.Location;
import ru.practicum.repository.*;
import ru.practicum.service.category.CategoryService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constant.Constant.PATTERN_DATE;
import static ru.practicum.model.QEvent.event;

/**
 * The type Event service.
 */
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

    private final UserServiceClient userServiceClient;

    private final LocationMapper locationMapper;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_DATE);

    EventRepository eventRepository;
    RequestServiceClient requestServiceClient;
    EventMapper eventMapper;
    CategoryService categoryService;
    UtilEventClass utilEventClass;
    LocationRepository locationRepository;
    SearchEventRepository searchEventRepository;
    CategoryRepository categoryRepository;
    StatClient statClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            RequestServiceClient requestServiceClient,
                            EventMapper eventMapper, CategoryService categoryService, UtilEventClass utilEventClass,
                            LocationRepository locationRepository, SearchEventRepository searchEventRepository,
                            CategoryRepository categoryRepository, StatClient statClient,
                            LocationMapper locationMapper,
                            UserServiceClient userServiceClient) {
        this.eventRepository = eventRepository;
        this.requestServiceClient = requestServiceClient;
        this.eventMapper = eventMapper;
        this.categoryService = categoryService;
        this.utilEventClass = utilEventClass;
        this.locationRepository = locationRepository;
        this.searchEventRepository = searchEventRepository;
        this.categoryRepository = categoryRepository;
        this.statClient = statClient;
        this.locationMapper = locationMapper;
        this.userServiceClient = userServiceClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findByInitiatorId(userId, PageRequest.of(from, size));

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        CategoryDto category = categoryService.getCategoryById(eventDto.getCategory());
        Location location = locationMapper.toLocation(eventDto.getLocation());
        locationRepository.save(location);
        Event event = utilEventClass.toEventFromNewEventDto(eventDto, userId, category, location);

        if (eventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }
        if (eventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (eventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event = eventRepository.save(event);
        return utilEventClass.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found!", ""));
        return utilEventClass.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto changeEvent(Long userId, Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found!", ""));
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("event with id=" + eventId + "published and cannot be changed", "");
        }
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }
        utilEventClass.updateEventFromDto(event, eventDto);

        event = eventRepository.save(event);
        return utilEventClass.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Integer from, Integer size) {
        checkDateTime(rangeStart, rangeEnd);
        SearchEventsParamAdmin searchEventsParamAdmin = SearchEventsParamAdmin.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        List<Event> events = searchEventRepository.getEventsByParamForAdmin(searchEventsParamAdmin);

        for (Event event : events) {
            event.setConfirmedRequests(requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
        }


        return events.stream()
                .map(utilEventClass::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found!", ""));
        Category category;
        if (request.getCategory() != null) {
            category = categoryRepository.findById(request.getCategory()).orElseThrow(() ->
                    new NotFoundException("Category with id=" + request.getCategory() + " not found!", ""));
        } else {
            category = event.getCategory();
        }
        Location location = checkAndSaveLocation(locationMapper.toLocation(request.getLocation()));
        checkTimeBeforeStart(request.getEventDate(), 1);
        checkTimeBeforeStart(event.getEventDate(), 1);

        if (AdminStateAction.PUBLISH_EVENT.equals(request.getStateAction())) {
            if (event.getState().equals(EventState.PENDING)) {
                event = utilEventClass.updateEvent(event, request, category, location);
                event.setPublishedOn(LocalDateTime.now());
                event.setState(EventState.PUBLISHED);
            } else {
                throw new ConflictException("Event is not PENDING!", "");
            }
        } else if (AdminStateAction.REJECT_EVENT.equals(request.getStateAction())) {
            if (!event.getState().equals(EventState.PUBLISHED)) {
                event = utilEventClass.updateEvent(event, request, category, location);
                event.setState(EventState.CANCELED);
            } else {
                throw new ConflictException("PUBLISHED events can't be cancelled!", "event should be PENDING or CANCELED");

            }
        }
        return utilEventClass.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, String sort, int from, int size, String clientIp) {

        // Если все параметры отсутствуют, то возвращаем пустой список и записываем статистику
        if (Boolean.TRUE.equals(text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null
                                && !onlyAvailable && sort == null && from == 0) && size == 10) {

            log.info("==> Статистика: вызов метода getEvents с пустыми параметрами от клиента {}", clientIp);

            // Записываем статистику
            saveEventsRequestToStats(clientIp);

            // Возвращаем пустой список
            return Collections.emptyList();
        }

        rangeStart = (rangeStart == null) ? LocalDateTime.of(1970, 1, 1, 0, 0) : rangeStart;
        rangeEnd = (rangeEnd == null) ? LocalDateTime.of(2099, 12, 31, 23, 59) : rangeEnd;

        log.info("Параметры для SQL: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}",
                text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAllEvents(
                text,
                (categories == null) ? new Long[0] : categories.toArray(new Long[0]), // Передаем пустой массив, если null
                paid,
                rangeStart,
                rangeEnd
        );

        // Получаем количество подтвержденных заявок для каждого мероприятия
        Map<Long, Long> eventRequestCounts = new HashMap<>();
        for (Event event : events) {
            long confirmedRequests = requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId());
            eventRequestCounts.put(event.getId(), confirmedRequests);
        }

        // Фильтруем мероприятия, если onlyAvailable = true
        List<Event> filteredEvents = new ArrayList<>(events.stream()
                .filter(event -> {
                    Long confirmedRequests = eventRequestCounts.get(event.getId());
                    return !onlyAvailable || event.getParticipantLimit() == null || confirmedRequests < event.getParticipantLimit();
                })
                .toList());

        // Получаем статистику просмотров для каждого мероприятия с помощью StatClient
        Map<Long, Long> eventViews = new HashMap<>();
        List<String> uris = filteredEvents.stream()
                .map(event -> "/events/" + event.getId()) // Получаем URI для каждого мероприятия
                .toList();

        // Запрашиваем статистику просмотров с использованием StatClient
        List<ViewStatsDto> viewStats = statClient.getStats(rangeStart.toString(), rangeEnd.toString(), uris, true);

        // Обработка случая, если статистика отсутствует
        if (viewStats == null || viewStats.isEmpty()) {
            log.warn("Сервис статистики вернул пустой результат или null");
            viewStats = Collections.emptyList();
        }

        // Заполняем Map с количеством просмотров
        for (ViewStatsDto stat : viewStats) {
            Long eventId = Long.valueOf(stat.getUri().substring(stat.getUri().lastIndexOf("/") + 1));
            eventViews.put(eventId, stat.getHits());
        }

        // Сортировка
        if ("VIEWS".equalsIgnoreCase(sort)) {
            // Сортировка по количеству просмотров
            filteredEvents.sort((e1, e2) -> {
                long views1 = eventViews.getOrDefault(e1.getId(), 0L);
                long views2 = eventViews.getOrDefault(e2.getId(), 0L);
                return Long.compare(views2, views1); // по убыванию просмотров
            });
        } else if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            // Сортировка по дате события
            filteredEvents.sort(Comparator.comparing(Event::getEventDate));
        }
        log.info("Передаем запрос в статистику");

        // Логируем запрос в статистику
        saveEventsRequestToStats(clientIp);

        // Применяем пагинацию
        int start = Math.min(from, filteredEvents.size());
        int end = Math.min(from + size, filteredEvents.size());
        List<Event> paginatedEvents = filteredEvents.subList(start, end);

        return paginatedEvents.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getEventById(Long eventId, String clientIp) {
        // Проверка существования события
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " not found!", "")
        );

        // Проверка, что событие опубликовано
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not published yet!", "");
        }

        // Увеличение количества просмотров
        saveEventRequestToStats(event, clientIp);

        // Получение количества просмотров из статистики
        long views = getViewsFromStats(event);

        event.setViews(views);
        eventRepository.save(event);

        // Подсчет подтвержденных запросов
        long confirmedRequests = requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        // Создание DTO
        EventFullDto eventFullDto = utilEventClass.toEventFullDto(event);
        eventFullDto.setViews(views);
        eventFullDto.setConfirmedRequests(confirmedRequests);

        return eventFullDto;
    }

    private void saveEventsRequestToStats(String clientIp) {
        try {
            // Создание объекта для статистики
            log.info("Создание объекта для статистики");
            EndpointHitDto hitDto = new EndpointHitDto();
            hitDto.setApp("ewm-main-service");
            hitDto.setUri("/events");
            hitDto.setIp(clientIp);
            hitDto.setTimestamp(LocalDateTime.now());

            // Логируем успешный запрос
            log.info("Логируем запрос в статистику: URI={}, IP={}", hitDto.getUri(), hitDto.getIp());

            // Отправка статистики
            statClient.sendHit(hitDto);
        } catch (Exception e) {
            log.error("Ошибка при сохранении статистики для URI=/events, IP=" + clientIp, e);
        }
    }

    private void saveEventRequestToStats(Event event, String clientIp) {
        try {
            EndpointHitDto hitDto = new EndpointHitDto();
            hitDto.setApp("ewm-main-service");
            hitDto.setUri("/events/" + event.getId());
            hitDto.setIp(clientIp);
            hitDto.setTimestamp(LocalDateTime.now());

            statClient.sendHit(hitDto);
        } catch (Exception e) {
            log.error("Ошибка при сохранении статистики для события id=" + event.getId(), e);
        }
    }

    private long getViewsFromStats(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            String uri = "/events/" + event.getId();
            // Добавляем одну секунду к началу и завершению диапазона
            String start = event.getCreatedOn().minusSeconds(1).format(formatter);
            String end = LocalDateTime.now().plusSeconds(1).format(formatter);

            List<ViewStatsDto> stats = statClient.getStats(
                    start,
                    end,
                    List.of(uri),
                    true
            );

            return stats.stream()
                    .filter(stat -> stat.getUri().equals(uri))
                    .mapToLong(ViewStatsDto::getHits)
                    .sum();
        } catch (Exception e) {
            log.error("Ошибка при получении статистики просмотров для события id=" + event.getId(), e);
            return 0;
        }
    }


    private void checkDateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("start time can't be after end time", "time range is incorrect");
        }
    }

    private Location checkAndSaveLocation(Location newLocation) {
        if (newLocation == null) {
            return null;
        }
        Location location = locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElse(null);
        if (location == null) {
            return locationRepository.save(newLocation);
        }
        return location;
    }

    private void checkTimeBeforeStart(LocalDateTime checkingTime, Integer plusHour) {
        if (checkingTime != null && checkingTime.isBefore(LocalDateTime.now().plusHours(plusHour))) {
            throw new ValidationException("updated time should be " + plusHour + "ahead then current time!", "not enough time before event");
        }
    }

    @Override
    public EventFullDto getByIdInternal(long eventId) {
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id ", eventId + " not found"));
        savedEvent.setInitiatorId(userServiceClient.getById(savedEvent.getInitiatorId()).getId());
        return utilEventClass.toEventFullDto(savedEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllByPublic(EventSearchParams searchParams) {

        Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        PublicSearchParams publicSearchParams = searchParams.getPublicSearchParams();

        if (publicSearchParams.getText() != null) { //наличие поиска по тексту
            booleanExpression = booleanExpression.andAnyOf(
                    event.annotation.likeIgnoreCase(publicSearchParams.getText()),
                    event.description.likeIgnoreCase(publicSearchParams.getText())
            );
        }

        if (publicSearchParams.getCategories() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.category.id.in((publicSearchParams.getCategories())));
        }

        if (publicSearchParams.getPaid() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.paid.eq(publicSearchParams.getPaid()));
        }

        LocalDateTime rangeStart = publicSearchParams.getRangeStart();
        LocalDateTime rangeEnd = publicSearchParams.getRangeEnd();

        if (rangeStart != null && rangeEnd != null) { // наличие поиска дате события
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd)
            );
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart)
            );
            rangeEnd = rangeStart.plusYears(100);
        } else if (publicSearchParams.getRangeEnd() != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd)
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
        }

        if (rangeEnd == null && rangeStart == null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(LocalDateTime.now())
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
            rangeEnd = rangeStart.plusYears(100);
        }

        List<Event> eventListBySearch = eventListBySearch =
                eventRepository.findAll(booleanExpression, page).stream().toList();


        for (Event event : eventListBySearch) {

            Long view = 0L;

            event.setViews(view);
            event.setConfirmedRequests(
                    requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
            event.setLikes(eventRepository.countLikesByEventId(event.getId()));
        }

        return eventListBySearch.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }
}
