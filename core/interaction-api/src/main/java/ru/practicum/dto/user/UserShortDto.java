package ru.practicum.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * The type User short dto.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserShortDto {
    Long id;
    String name;
}
