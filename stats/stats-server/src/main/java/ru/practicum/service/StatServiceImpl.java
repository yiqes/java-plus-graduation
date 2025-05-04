package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.mapper.EndpointHitMapper.dtoToHit;
import static ru.practicum.mapper.EndpointHitMapper.toHitDto;

/**
 * The type Stat service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto hitDto) {
        return toHitDto(endpointHitRepository.save(dtoToHit(hitDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Получение статистики с параметрами: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<ViewStatsDto> stats;

        if (unique) {
            stats = uris.isEmpty() ?
                    endpointHitRepository.findStatAllWithUniqueIp(start, end) :
                    endpointHitRepository.findStatWithUniqueIp(start, end, uris);
        } else {
            stats = uris.isEmpty() ?
                    endpointHitRepository.findStatAllWithoutUniqueIp(start, end) :
                    endpointHitRepository.findStatWithoutUniqueIp(start, end, uris);
        }
        log.info("Полученные статистические данные: {}", stats);
        return new ArrayList<>(stats);
    }
}
