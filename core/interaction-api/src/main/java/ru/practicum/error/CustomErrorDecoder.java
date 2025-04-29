package ru.practicum.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());

        assert httpStatus != null;
        return switch (httpStatus) {
            case BAD_REQUEST -> new BadRequestException("Bad Request");
            case INTERNAL_SERVER_ERROR -> new InternalServerErrorException("Internal Server Error");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}