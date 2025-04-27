package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitMapper {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHit mapToEndpointHit(EndpointHitDto dto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(dto.getApp());
        endpointHit.setIp(dto.getIp());
        endpointHit.setUri(dto.getUri());
        LocalDateTime timestamp = LocalDateTime.parse(dto.getTimestamp(), dateTimeFormatter);
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }
}
