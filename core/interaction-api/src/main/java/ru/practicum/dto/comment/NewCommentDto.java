package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

/**
 * The type New comment dto.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {
    @NotBlank
    @Length(min = 1, max = 255)
    String text;
    @Positive
    Long parentComment;
}
