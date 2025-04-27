package ru.practicum.service;

import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.RequestParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatServiceImpl implements StatService {
    final EndpointHitRepository hitRepository;
    final EndpointHitMapper hitMapper;

    @Override
    @Transactional
    public void hit(EndpointHitDto endpointHitDto) {
        log.info("Запись {} в БД", endpointHitDto);
        EndpointHit endpointHit = hitMapper.mapToEndpointHit(endpointHitDto);
        hitRepository.save(endpointHit);
        log.info("Объект {} успешно сохранен в БД", endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> stats(RequestParamDto params) {
        log.info("Запрос статистики {}", params);

        if (params.getUnique() == null) {
            params.setUnique(false);
        }

        if (params.getStart().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Время начала не может быть в прошлом");
        }

        List<ViewStatsDto> statsToReturn;

        boolean paramsIsNotExists = params.getUris() == null || params.getUris().isEmpty();

        if (!params.getUnique()) {
            if (paramsIsNotExists) {
                statsToReturn = hitRepository.getAllStats(params.getStart(), params.getEnd());
            } else {
                statsToReturn = hitRepository.getStats(params.getUris(), params.getStart(), params.getEnd());
            }
        } else {
            if (paramsIsNotExists) {
                statsToReturn = hitRepository.getAllStatsUniqueIp(params.getStart(), params.getEnd());
            } else {
                statsToReturn = hitRepository.getStatsUniqueIp(params.getUris(), params.getStart(), params.getEnd());
            }
        }

        log.info("Данные статистики {} успешно считаны из БД", statsToReturn);
        return statsToReturn;
    }
}
