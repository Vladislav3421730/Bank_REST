package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.request.LoginUserRequestDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.exception.LoginFailedException;
import com.example.bankcards.exception.PasswordsNotTheSameException;
import com.example.bankcards.exception.RegistrationFailedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.model.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtAccessTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtAccessTokenUtils jwtAccessTokenUtils;

    @Override
    public JwtResponseDto createAuthToken(LoginUserRequestDto user) {
        try {
            log.info("Attempting authentication for user: {}", user.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            log.error("error: {}", badCredentialsException.getMessage());
            throw new LoginFailedException("Invalid username or password");
        }

        User userDB = userRepository.findByUsername(user.getUsername()).orElseThrow(() ->
                new UserNotFoundException(String.format("User with username %s was not found", user.getUsername())));

        log.info("User {} authenticated successfully", user.getUsername());
        return new JwtResponseDto(jwtAccessTokenUtils.generateAccessToken(userDB));
    }

    @Override
    public void registerUser(RegisterUserRequestDto user) {
        log.info("Starting registration process for user: {}", user.getUsername());

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            log.error("Password mismatch for user: {}", user.getUsername());
            throw new PasswordsNotTheSameException("Passwords should be the same");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.error("Email {} is already in use", user.getUsername());
            throw new RegistrationFailedException(String.format("User with login %s already exists in the system", user.getUsername()));
        }
        userService.save(user);
    }

}
