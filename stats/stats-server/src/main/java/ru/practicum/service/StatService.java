package ru.practicum.service;

import ru.practicum.dto.EndpointHitResponseDto;
import ru.practicum.dto.EndpointHitSaveRequestDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Stat service.
 */
public interface StatService {

    /**
     * Save info endpoint hit response dto.
     *
     * @param endpointHitSaveRequestDto the endpoint hit save request dto
     * @return the endpoint hit response dto
     */
    EndpointHitResponseDto saveInfo(EndpointHitSaveRequestDto endpointHitSaveRequestDto);

    /**
     * Gets stats.
     *
     * @param start  the start
     * @param end    the end
     * @param uris   the uris
     * @param unique the unique
     * @return the stats
     */
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
