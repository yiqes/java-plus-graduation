package ru.practicum.exception;

import lombok.Getter;

/**
 * The type Conflict exception.
 */
@Getter
public class ConflictException extends RuntimeException {

    /**
     * The Reason.
     */
    final String reason;

    /**
     * Instantiates a new Conflict exception.
     *
     * @param message the message
     * @param reason  the reason
     */
    public ConflictException(final String message, final String reason) {
        super(message);
        this.reason = reason;
    }

}