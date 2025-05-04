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
import java.util.Map;
import java.util.stream.Collectors;

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

//        List<EndpointHit> data;
//        List<ViewStatsDto> result = new ArrayList<>();
//        if ((start.isBlank() || end.isBlank())) {
//            data = endpointHitRepository.findAllByUriIn(uris);
//        } else {
//            LocalDateTime localDateTimeStart = LocalDateTime.parse(start, FORMATTER);
//            LocalDateTime localDateTimeEnd = LocalDateTime.parse(end, FORMATTER);
//            if (!localDateTimeStart.isBefore(localDateTimeEnd)) {
//                throw new IllegalArgumentException("start must be before end");
//            }
//            data = (uris == null || uris.isEmpty()) ? endpointHitRepository.getStat(localDateTimeStart, localDateTimeEnd) :
//                    endpointHitRepository.getStatByUris(localDateTimeStart, localDateTimeEnd, uris);
//        }
//        Map<String, Map<String, List<EndpointHit>>> mapByAppAndUri = data.stream()
//                .collect(Collectors.groupingBy(EndpointHit::getApp,
//                        Collectors.groupingBy(EndpointHit::getUri)));
//        mapByAppAndUri.forEach((appKey, mapUriValue) -> mapUriValue.forEach((uriKey, hitsValue) -> {
//            ViewStatsDto hitStat = new ViewStatsDto();
//            hitStat.setApp(appKey);
//            hitStat.setUri(uriKey);
//            List<String> ips = hitsValue.stream().map(EndpointHit::getIp).toList();
//            Integer hits = unique ? ips.stream().distinct().toList().size() : ips.size();
//            hitStat.setHits(hits);
//            result.add(hitStat);
//        }));
//        return result.stream().sorted(Comparator.comparingInt(ViewStatsDto::getHits).reversed()).toList();
    }
}
