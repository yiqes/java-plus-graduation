package ru.practicum.controller.pub;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.EventServiceClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.service.event.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/feign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController implements EventServiceClient {

    final EventService service;

    @GetMapping(path = "/exist")
    @Override
    public boolean existEventByCategoryId(@RequestParam("categoryId") Long categoryId) {
        return service.existEventByCategoryId(categoryId);
    }

    @GetMapping
    @Override
    public EventFullDto findById(@RequestParam("eventId") Long eventId) {
        return service.findById(eventId);
    }
}
