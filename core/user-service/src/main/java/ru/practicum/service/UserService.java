package ru.practicum.service;

import ru.practicum.controller.AdminUsersGetAllParams;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;
import java.util.Map;

/**
 * The interface User service.
 */
public interface UserService {
    /**
     * Add user dto.
     *
     * @param newUserRequest the new user request
     * @return the user dto
     */
    UserDto add(NewUserRequest newUserRequest);


    List<UserDto> getUsers(AdminUsersGetAllParams adminUsersGetAllParams);

    void checkExistence(long userId);

    /**
     * Delete.
     *
     * @param userId the user id
     */
    void delete(long userId);

    UserDto getById(long userId);

    Map<Long, UserShortDto> getByIds(List<Long> userIds);
}
