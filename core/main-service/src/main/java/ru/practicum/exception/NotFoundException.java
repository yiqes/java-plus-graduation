package ru.practicum.exception;

import lombok.Getter;

/**
 * The type Not found exception.
 */
@Getter
public class NotFoundException extends RuntimeException {
    /**
     * The Reason.
     */
    final String reason;

    /**
     * Instantiates a new Not found exception.
     *
     * @param message the message
     * @param reason  the reason
     */
    public NotFoundException(final String message, final String reason) {
        super(message);
        this.reason = reason;
    }

}