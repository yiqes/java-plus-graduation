package ru.practicum.exception;

import lombok.Getter;

/**
 * The type Validation exception.
 */
@Getter
public class ValidationException extends RuntimeException {
    /**
     * The Reason.
     */
    final String reason;

    /**
     * Instantiates a new Validation exception.
     *
     * @param message the message
     * @param reason  the reason
     */
    public ValidationException(final String message, final String reason) {
        super(message);
        this.reason = reason;
    }

}