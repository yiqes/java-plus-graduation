package ru.practicum.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.AdminUsersGetAllParams;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.NewUserMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserMapper userMapper;
    UserRepository userRepository;
    NewUserMapper newUserRequestMapper;

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(newUserRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new ConflictException("could not execute statement; SQL [n/a]; constraint [uq_email]; " +
                    "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute " +
                    "statement", "Integrity constraint has been violated.");
        }
        User user = newUserRequestMapper.fromNewUserRequest(newUserRequest);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(AdminUsersGetAllParams adminUsersGetAllParams) {
        PageRequest pageRequest = PageRequest.of(
                adminUsersGetAllParams.from(), adminUsersGetAllParams.size());
        List<User> userSearchList = adminUsersGetAllParams.ids() == null
                ? userRepository.findAll(pageRequest).stream().toList()
                : userRepository.findAllByIdIn(adminUsersGetAllParams.ids(), pageRequest);

        return userSearchList.stream()
                .map(userMapper::toUserDto).toList();
    }

    @Override
    public void delete(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found."));
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getById(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User with id ",  userId + " not found"));
    }

    @Override
    public void checkExistence(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id ", userId + " not found"));
    }

    @Override
    public Map<Long, UserDto> getByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, userMapper::toUserDto));
    }
}
