//package ru.practicum.exception;
//
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.bind.support.WebExchangeBindException;
//import org.springframework.web.method.annotation.HandlerMethodValidationException;
//
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.time.LocalDateTime;
//
//@RestControllerAdvice
//public class ErrorHandler {
//    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class,
//            ConstraintViolationException.class, WebExchangeBindException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleMethodArgumentValidException(Exception exception) {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(stringWriter);
//        exception.printStackTrace(printWriter);
//        String errors = stringWriter.toString();
//        String cause = "Ошибка при вводе значений";
//        //log.info("{}: {}", cause, exception.getMessage());
//        return ApiError.builder()
//                .errors(errors)
//                .message(exception.getMessage())
//                .reason(cause)
//                .status(HttpStatus.BAD_REQUEST.toString())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    @ExceptionHandler({ValidationException.class, DataAccessException.class, WrongSortMethodException.class,
//            HandlerMethodValidationException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleValidationException(Exception exception) {
//        String cause = "Ошибка при валидации данных";
//        //log.info("{}: {}", cause, exception.getMessage());
//        return ApiError.builder()
//                .message(exception.getMessage())
//                .reason(cause)
//                .status(HttpStatus.BAD_REQUEST.toString())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public ApiError handleDataIntegrityViolationException(Exception exception) {
//        String cause = "Нарушение целостности данных";
//        //log.info("{}: {}", cause, exception.getMessage());
//        return ApiError.builder()
//                .message(exception.getMessage())
//                .reason(cause)
//                .status(HttpStatus.CONFLICT.toString())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ApiError handleNotFoundException(NotFoundException exception) {
//        String cause = "Ошибка при поиске данных";
//        //log.info("{}: {}", cause, exception.getMessage());
//        return ApiError.builder()
//                .message(exception.getMessage())
//                .reason(cause)
//                .status(HttpStatus.NOT_FOUND.toString())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiError handleException(Exception exception) {
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(stringWriter);
//        exception.printStackTrace(printWriter);
//        String errors = stringWriter.toString();
//        String cause = "Внутренняя ошибка сервера";
//        //log.info("{}: {}", cause, exception.getMessage());
//        return ApiError.builder()
//                .errors(errors)
//                .message(exception.getMessage())
//                .reason(cause)
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//}
