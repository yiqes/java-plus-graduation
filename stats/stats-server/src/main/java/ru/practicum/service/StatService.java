package ru.practicum.service;


import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.RequestParamDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatService {
    void hit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> stats(RequestParamDto params);
}
