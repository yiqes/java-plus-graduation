package ru.practicum.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping(path = "/users")
    UserDto getBy(@RequestParam("ids") long userId);

    @GetMapping(path = "/admin/users")
    List<UserDto> getAllBy(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    @GetMapping(path = "/users/mapping")
    Map<Long, UserShortDto> getAllBuIds(@RequestParam List<Long> ids);

}