package ru.practicum.service.user;

import ru.practicum.controller.admin.AdminUsersGetAllParams;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

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

    /**
     * Delete.
     *
     * @param userId the user id
     */
    void delete(long userId);
}
