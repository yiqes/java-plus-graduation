package ru.practicum.exception;

import lombok.Getter;

/**
 * The type Forbidden exception.
 */
@Getter
public class ForbiddenException extends RuntimeException {
    /**
     * The Reason.
     */
    final String reason;

    /**
     * Instantiates a new Forbidden exception.
     *
     * @param message the message
     * @param reason  the reason
     */
    public ForbiddenException(final String message, final String reason) {
        super(message);
        this.reason = reason;
    }

}