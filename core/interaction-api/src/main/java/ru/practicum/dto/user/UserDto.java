package ru.practicum.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * The type User dto.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    String name;
    String email;
}
