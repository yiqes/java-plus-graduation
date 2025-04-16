package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The type Error handler.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    /**
     * Handle throwable error response.
     *
     * @param throwable the throwable
     * @return the error response
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable throwable) {
        log.info("500 {}", throwable.getMessage(), throwable);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage(), stackTrace);
    }

    /**
     * Handle illegal argument exception error response.
     *
     * @param e the e
     * @return the error response
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.info("500 {}", e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), stackTrace);
    }
}
