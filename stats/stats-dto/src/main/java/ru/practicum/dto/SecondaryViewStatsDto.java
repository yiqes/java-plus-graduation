package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Secondary view stats dto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondaryViewStatsDto {
    /**
     * The App.
     */
    String app;
    /**
     * The Uri.
     */
    String uri;
    /**
     * The Hits.
     */
    Long hits;
}
