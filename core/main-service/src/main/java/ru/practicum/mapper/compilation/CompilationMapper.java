package ru.practicum.mapper.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.model.Compilation;

import java.util.List;

/**
 * The interface Compilation mapper.
 */
@Mapper(componentModel = "spring", uses = {CompilationsMapperUtil.class})
public interface CompilationMapper {

    /**
     * To compilation dto compilation dto.
     *
     * @param compilation the compilation
     * @return the compilation dto
     */
    @Mapping(target = "events", qualifiedByName = {"CompilationsMapperUtil", "getEventShortDtos"}, source = "compilation")
    CompilationDto toCompilationDto(Compilation compilation);

    /**
     * To compilation dtos list.
     *
     * @param compilations the compilations
     * @return the list
     */
    List<CompilationDto> toCompilationDtos(List<Compilation> compilations);
}
