package ru.practicum.service.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicSearchParams {

    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;

}