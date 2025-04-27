package ru.practicum.exception;

public class WrongSortMethodException extends RuntimeException {
    public WrongSortMethodException(String message) {
        super(message);
    }
}