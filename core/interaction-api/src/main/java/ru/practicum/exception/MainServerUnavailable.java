package ru.practicum.exception;

public class MainServerUnavailable extends RuntimeException {
    public MainServerUnavailable(String message, Exception e) {
        super(message, e);
    }
}
