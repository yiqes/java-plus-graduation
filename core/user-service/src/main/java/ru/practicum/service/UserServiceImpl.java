package ru.practicum.service;


import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.AdminUsersGetAllParams;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.NewUserMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.mapper.UserShortMapper;
import ru.practicum.model.QUser;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The type User service.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NewUserMapper newUserRequestMapper;
    private final UserMapper userMapper;
    private final UserShortMapper userShortMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, NewUserMapper newUserRequestMapper, UserMapper userMapper, UserShortMapper userShortMapper) {
        this.userRepository = userRepository;
        this.newUserRequestMapper = newUserRequestMapper;
        this.userMapper = userMapper;
        this.userShortMapper = userShortMapper;
    }

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
                .orElseThrow(() -> new NotFoundException("User with id= ", userId + " was not found"));
    }


    @Override
    public Map<Long, UserShortDto> getByIds(List<Long> userIds) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (userIds != null && !userIds.isEmpty()) {
            booleanBuilder.and(QUser.user.id.in(userIds));
        }
        return StreamSupport
                .stream(userRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toMap(
                        User::getId,
                        userShortMapper::toUserShortDto
                ));
    }

    @Override
    public void checkExistence(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id ", userId + " not found"));
    }
}
