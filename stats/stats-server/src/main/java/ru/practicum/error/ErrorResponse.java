package ru.practicum.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * The type Error response.
 */
@Getter
@Setter
public class ErrorResponse {
    /**
     * The Http status.
     */
    HttpStatus httpStatus;
    /**
     * The Message.
     */
    String message;
    /**
     * The Stack trace.
     */
    String stackTrace;

    /**
     * Instantiates a new Error response.
     *
     * @param httpStatus the http status
     * @param message    the message
     * @param stackTrace the stack trace
     */
    public ErrorResponse(HttpStatus httpStatus, String message, String stackTrace) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.stackTrace = stackTrace;
    }
}
