package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.request.BannedRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.exception.ManageYourselfException;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mapper.UserMapper;
import com.example.bankcards.model.Role;
import com.example.bankcards.model.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void save(RegisterUserRequestDto registerUserRequestDto) {
        log.info("Creating new user entity for: {}", registerUserRequestDto.getUsername());
        User userDB = userMapper.toNewEntity(registerUserRequestDto);

        Role role = roleRepository.findByName(Constants.USER_ROLE_NAME).orElseThrow(() -> {
            log.error("Role with name {} wasn't found", Constants.USER_ROLE_NAME);
            throw new RoleNotFoundException(String.format("Role with name %s wasn't found", Constants.USER_ROLE_NAME));
        });

        userDB.setPassword(passwordEncoder.encode(userDB.getPassword()));
        userDB.getRoles().add(role);
        userRepository.save(userDB);
        log.info("User {} registered successfully", userDB.getUsername());
    }

    @Override
    @Transactional
    public void delete(UUID id, String username) {
        User user = findAndCheck(id, username);
        userRepository.deleteById(id);
        log.info("User with id {} was successfully deleted", id);
    }

    @Override
    @Transactional
    public void banUser(UUID id, String username , BannedRequestDto bannedRequestDto) {
        User user = findAndCheck(id, username);
        user.setIsBan(bannedRequestDto.getBanned());
        userRepository.save(user);
        log.info("User was successfully updated, set ban status: {}", bannedRequestDto.getBanned());
    }

    @Override
    public Page<UserDto> findAll(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User with id {} wasn't found", id);
                    throw new UserNotFoundException(String.format("User with id %s wasn't found", id));
                });
    }

    public User findAndCheck(UUID id, String username) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User with id {} wasn't found", id);
            throw new UserNotFoundException(String.format("User with id %s wasn't found", id));
        });

        if(user.getUsername().equals(username)) {
            log.info("User {} try bun/delete himself", username);
            throw new ManageYourselfException("You can't delete/ban yourself");
        }
        return user;
    }

}
