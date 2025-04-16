package ru.practicum.service.user;

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

    /**
     * Gets users.
     *
     * @param ids  the ids
     * @param from the from
     * @param size the size
     * @return the users
     */
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    /**
     * Delete.
     *
     * @param userId the user id
     */
    void delete(long userId);
}
