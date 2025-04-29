package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

/**
 * The type Admin compilation controller.
 */
@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {

    private static final String PATH = "comp-id";
    private final CompilationService compilationService;

    /**
     * Add compilation dto.
     *
     * @param newCompilationDto the new compilation dto
     * @return the compilation dto
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto add(@RequestBody @Valid CompilationDto newCompilationDto) {
        log.info("==> Admin add by newCompilationDto = {}", newCompilationDto);
        //CompilationDto compilationDto = compilationService.add(newCompilationDto);
        log.info("<== Admin add result: {}", newCompilationDto);

        return newCompilationDto;
    }

    /**
     * Delete.
     *
     * @param compId the comp id
     */
    @DeleteMapping("/{comp-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(PATH) @Positive long compId) {
        log.info("==> Admin delete for compId = {}", compId);
        compilationService.delete(compId);
    }

    /**
     * Update compilation dto.
     *
     * @param compId                   the comp id
     * @param updateCompilationRequest the update compilation request
     * @return the compilation dto
     */
    @PatchMapping("/{comp-id}")
    CompilationDto update(@PathVariable(PATH) @Positive long compId,
                          @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("==> Admin updating category: {}", updateCompilationRequest);
        CompilationDto compilationDto = compilationService.update(compId, updateCompilationRequest);
        log.info("<== Admin updating user: {}", compilationDto);

        return compilationDto;
    }
}
