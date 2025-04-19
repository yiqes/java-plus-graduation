package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitResponseDto;
import ru.practicum.dto.EndpointHitSaveRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Stat controller.
 */
@RestController
@Slf4j
public class StatController {

    private final StatService service;

    /**
     * Instantiates a new Stat controller.
     *
     * @param service the service
     */
    @Autowired
    public StatController(StatService service) {
        this.service = service;
    }

    /**
     * Save info endpoint hit response dto.
     *
     * @param endpointHitDto the endpoint hit dto
     * @return the endpoint hit response dto
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitResponseDto saveInfo(@RequestBody EndpointHitSaveRequestDto endpointHitDto) {
        log.info("/POST: Запрос на сохранение {}", endpointHitDto);
        return service.saveInfo(endpointHitDto);
    }

    /**
     * Gets stats.
     *
     * @param start  the start
     * @param end    the end
     * @param uris   the uris
     * @param unique the unique
     * @return the stats
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique
    ) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Время не может быть Null");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }
        if (uris == null) {
            uris = new ArrayList<>();
        }
        log.info("/GET запрос на получение статистики");
        return service.getStats(start, end, uris, unique);
    }
}
