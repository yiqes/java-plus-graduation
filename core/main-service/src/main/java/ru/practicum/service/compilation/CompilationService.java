package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

/**
 * The interface Compilation service.
 */
public interface CompilationService {

    /**
     * Add compilation dto.
     *
     * @param newCompilationDto the new compilation dto
     * @return the compilation dto
     */
    CompilationDto add(NewCompilationDto newCompilationDto);

    /**
     * Update compilation dto.
     *
     * @param compId                   the comp id
     * @param updateCompilationRequest the update compilation request
     * @return the compilation dto
     */
    CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest);

    /**
     * Get compilation dto.
     *
     * @param compId the comp id
     * @return the compilation dto
     */
    CompilationDto get(long compId);

    /**
     * Gets compilations.
     *
     * @param pinned the pinned
     * @param from   the from
     * @param size   the size
     * @return the compilations
     */
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    /**
     * Delete.
     *
     * @param compId the comp id
     */
    void delete(long compId);
}
