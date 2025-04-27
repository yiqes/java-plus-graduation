package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.EventFullDto;

@FeignClient(name = "event-service", path = "/events/feign")
public interface EventServiceClient {

    @GetMapping(path = "/exist")
    boolean existEventByCategoryId(@RequestParam("categoryId") Long id);

    @GetMapping
    EventFullDto findById(@RequestParam("eventId") Long eventId);

}