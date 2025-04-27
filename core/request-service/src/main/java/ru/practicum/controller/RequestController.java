package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.RequestServiceClient;
import ru.practicum.enums.RequestStatus;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController implements RequestServiceClient {

    final RequestService requestService;

    @GetMapping(path = "/count")
    @Override
    public long countAllByEventIdAndStatusIs(@RequestParam long eventId, @RequestParam String status) {
        return requestService.countAllByEventIdAndStatusIs(eventId, RequestStatus.valueOf(status));
    }

    @GetMapping(path = "/confirmed")
    @Override
    public Map<Long, Long> getConfirmedRequestMap(List<Long> eventIds) {
        return requestService.getConfirmedRequestsMap(eventIds);
    }
}
