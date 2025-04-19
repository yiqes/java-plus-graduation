package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.*;
import ru.practicum.model.EndpointHit;

/**
 * The type Endpoint hit mapper.
 */
@Mapper(componentModel = "spring")
public abstract class EndpointHitMapper {

    /**
     * To endpoint hit endpoint hit.
     *
     * @param endpointHitSaveRequestDto the endpoint hit save request dto
     * @return the endpoint hit
     */
    public EndpointHit toEndpointHit(EndpointHitSaveRequestDto endpointHitSaveRequestDto) {
        if (endpointHitSaveRequestDto == null) {
            return null;
        }
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitSaveRequestDto.getApp());
        endpointHit.setUri(endpointHitSaveRequestDto.getUri());
        endpointHit.setIp(endpointHitSaveRequestDto.getIp());
        endpointHit.setTimestamp(endpointHitSaveRequestDto.getTimestamp());
        return endpointHit;
    }

    /**
     * To response dto endpoint hit response dto.
     *
     * @param endpointHit the endpoint hit
     * @return the endpoint hit response dto
     */
    public EndpointHitResponseDto toResponseDto(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }
        EndpointHitResponseDto endpointHitResponseDto = new EndpointHitResponseDto();
        endpointHitResponseDto.setApp(endpointHit.getApp());
        endpointHitResponseDto.setUri(endpointHit.getUri());
        endpointHitResponseDto.setIp(endpointHit.getIp());
        return endpointHitResponseDto;
    }
}
