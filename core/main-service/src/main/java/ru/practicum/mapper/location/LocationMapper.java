package ru.practicum.mapper.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

/**
 * The interface Location mapper.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper {
    /**
     * To location dto location dto.
     *
     * @param location the location
     * @return the location dto
     */
    LocationDto toLocationDto(Location location);

    /**
     * To location location.
     *
     * @param locationDto the location dto
     * @return the location
     */
    @Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto locationDto);
}
