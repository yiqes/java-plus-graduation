package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class PublicUserController implements UserServiceClient {

    final UserService service;

    @Override
    @GetMapping
    public UserDto getBy(@RequestParam("ids") long userId) {
        return service.getById(userId);
    }

    @Override
    public List<UserDto> getAllBy(List<Long> ids, int from, int size) {
        return List.of();
    }

    @Override
    @GetMapping(path = "/mapping")
    public Map<Long, UserShortDto> getAllBuIds(@RequestParam List<Long> ids) {
        return service.getByIds(ids);
    }
}
