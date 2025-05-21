package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.request.BannedRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.util.Constants;
import com.example.bankcards.util.mapper.UserMapper;
import com.example.bankcards.model.Role;
import com.example.bankcards.model.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private static RegisterUserRequestDto registerUserRequestDto;
    private static User userEntity;
    private static Role userRole;
    private static UUID userId;
    private static String username;
    private static User existingUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();

        registerUserRequestDto = RegisterUserRequestDto.builder()
                .username("test")
                .password("plainPassword")
                .build();

        username = "login";

        userEntity = User.builder()
                .id(userId)
                .username(registerUserRequestDto.getUsername())
                .password(registerUserRequestDto.getPassword())
                .build();

        userRole = Role.builder()
                .name(Constants.USER_ROLE_NAME)
                .build();

        existingUser = User.builder()
                .id(userId)
                .username("username")
                .isBan(false)
                .build();
    }

    @Test
    void save_ShouldSaveUserWithEncodedPasswordAndRole() {
        when(userMapper.toNewEntity(registerUserRequestDto)).thenReturn(userEntity);
        when(roleRepository.findByName(Constants.USER_ROLE_NAME)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerUserRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.save(registerUserRequestDto);

        assertEquals("encodedPassword", userEntity.getPassword());
        assertTrue(userEntity.getRoles().contains(userRole));

        verify(userMapper).toNewEntity(registerUserRequestDto);
        verify(roleRepository).findByName(Constants.USER_ROLE_NAME);
        verify(passwordEncoder).encode(registerUserRequestDto.getPassword());
        verify(userRepository).save(userEntity);
    }

    @Test
    void save_ShouldThrowRoleNotFoundException_WhenRoleNotFound() {
        when(userMapper.toNewEntity(registerUserRequestDto)).thenReturn(userEntity);
        when(roleRepository.findByName(Constants.USER_ROLE_NAME)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () ->
                userService.save(registerUserRequestDto));

        assertTrue(exception.getMessage().contains(Constants.USER_ROLE_NAME));

        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId, username);

        verify(userRepository).deleteById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void delete_ShouldThrowUserNotFoundException_WhenUserNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.delete(userId, username));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void banUser_ShouldUpdateUserBanStatus() {
        BannedRequestDto bannedRequestDto = new BannedRequestDto(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenAnswer(invocation -> invocation.getArgument(0));
        existingUser.setIsBan(bannedRequestDto.getBanned());

        userService.banUser(userId, username, bannedRequestDto);

        assertTrue(existingUser.getIsBan());

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void banUser_ShouldThrowUserNotFoundException_WhenUserNotExists() {
        BannedRequestDto bannedRequestDto = new BannedRequestDto(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.banUser(userId, username, bannedRequestDto));

        assertTrue(exception.getMessage().contains(userId.toString()));

        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnPageOfUserDto() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = List.of(userEntity);

        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(userMapper.toDto(userEntity)).thenReturn(UserDto.builder().id(userId).username(userEntity.getUsername()).build());

        Page<UserDto> result = userService.findAll(pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals(userId, result.getContent().get(0).getId());

        verify(userRepository).findAll(pageRequest);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void findById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(UserDto.builder().id(userId).username(userEntity.getUsername()).build());

        UserDto result = userService.findById(userId);

        assertEquals(userId, result.getId());
        assertEquals(userEntity.getUsername(), result.getUsername());

        verify(userRepository).findById(userId);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void findById_ShouldThrowUserNotFoundException_WhenUserNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.findById(userId));

        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userMapper, never()).toDto(any());
    }
}

