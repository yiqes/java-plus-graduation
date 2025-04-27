package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service", path = "/requests")
public interface RequestServiceClient {
    @GetMapping(path = "/count")
    long countAllByEventIdAndStatusIs(@RequestParam("eventId") long eventId,
                                      @RequestParam String status);

    @GetMapping("/confirmed")
    Map<Long, Long> getConfirmedRequestMap(@RequestParam List<Long> eventIds);
}
