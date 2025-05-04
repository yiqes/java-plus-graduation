package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;

import static ru.practicum.util.Constants.FORMATTER;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHitDto toHitDto(EndpointHit hit) {
        String dateTime = hit.getTimestamp().format(FORMATTER);

        return new EndpointHitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                dateTime
        );
    }

    public static EndpointHit dtoToHit(EndpointHitDto hitDto) {

        LocalDateTime localDateTime = LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER);
        EndpointHit hit = new EndpointHit();
        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(localDateTime);
        return hit;
    }
}