package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * The type Stat client.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class StatClient {
    private final FeignStatClient feignStatClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Send hit.
     *
     * @param endpointHitDto the endpoint hit dto
     */
    public void sendHit(EndpointHitDto endpointHitDto) {
        feignStatClient.hit(endpointHitDto);
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
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        try {
            return feignStatClient.stats(start.formatted(), end.formatted(), uris, unique);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}