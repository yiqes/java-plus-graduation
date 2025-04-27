package ru.practicum;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.FeignStatClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.RequestParamDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatController implements FeignStatClient {
    final StatService statService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        statService.hit(endpointHitDto);
    }

    @GetMapping("/stats")
    @Override
    public List<ViewStatsDto> stats(@RequestParam(value = "start", required = false) String start,
                                    @RequestParam(value = "end", required = false) String end,
                                    @RequestParam(value = "uris", required = false) List<String> uris,
                                    @RequestParam(value = "unique", required = false) boolean unique) {
        if (start == null) {
            throw new ValidationException("Параметр start не может быть null");
        }

        if (end == null) {
            throw new ValidationException("Параметр end не может быть null");
        }
        RequestParamDto requestParamDto = new RequestParamDto(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter), uris, unique);
        return statService.stats(requestParamDto);
    }
}
