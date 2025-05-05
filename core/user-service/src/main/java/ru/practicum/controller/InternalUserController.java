package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {
    private final UserService userService;

    @GetMapping("/{user-id}")
    public UserDto getById(@PathVariable("user-id") long userId) {
        log.info("|| ==> GET Getting user by id: {}", userId);
        UserDto userDto = userService.getById(userId);
        log.info("|| <== GET Returning user: {}", userDto);
        return userDto;
    }

    @GetMapping("/{user-id}/check")
    public void checkExistence(@PathVariable("user-id") long userId) {
        log.info("==> GET. Checking exist for User: {}", userId);
        userService.checkExistence(userId);
        log.info("|==| GET. User exist: {}", true);
    }

    @GetMapping("/all")
    public Map<Long, UserDto> getAll(@RequestParam List<Long> userIds) {
        log.info("==> GET. Getting users by ids: {}", userIds);
        Map<Long, UserDto> users = userService.getByIds(userIds);
        log.info("<== GET. Returning users by ids: {}", userIds);
        return users;
    }
}
