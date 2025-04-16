package ru.practicum.mapper.compilation;


import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.event.EventMapper;
import ru.practicum.model.Compilation;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Compilations mapper util.
 */
@Component
@Named("CompilationsMapperUtil")
@RequiredArgsConstructor
public class CompilationsMapperUtil {
    private final EventMapper eventMapper;

    /**
     * Gets event short dtos.
     *
     * @param compilation the compilation
     * @return the event short dtos
     */
    @Named("getEventShortDtos")
    List<EventShortDto> getEventShortDtos(Compilation compilation) {
        if (compilation.getEvents().isEmpty()) {
            return new ArrayList<>();
        }
        return compilation.getEvents().stream().map(eventMapper::toEventShortDto).toList();
    }
}