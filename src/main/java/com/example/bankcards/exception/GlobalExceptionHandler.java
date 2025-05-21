package com.example.bankcards.exception;

import com.example.bankcards.dto.error.AppErrorDto;
import com.example.bankcards.dto.error.FieldErrorDto;
import com.example.bankcards.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorDto> handleValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(new FieldErrorDto(errors,400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<AppErrorDto> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        return new ResponseEntity<>(new AppErrorDto(entityNotFoundException.getMessage(),404), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<AppErrorDto> handleRegistrationFailedException(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(new AppErrorDto(registrationFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<AppErrorDto> handleLoginFailedException(LoginFailedException loginFailedException) {
        return new ResponseEntity<>(new AppErrorDto(loginFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<AppErrorDto> handleDisabledException(DisabledException disabledException) {
        return new ResponseEntity<>(new AppErrorDto(disabledException.getMessage(),403), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PasswordsNotTheSameException.class)
    public ResponseEntity<AppErrorDto> handlePasswordsNotTheSameException(PasswordsNotTheSameException passwordsNotTheSameException) {
        return new ResponseEntity<>(new AppErrorDto(passwordsNotTheSameException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EncryptionFailedException.class)
    public ResponseEntity<AppErrorDto> handleEncryptionFailedException(EncryptionFailedException encryptionFailedException) {
        return new ResponseEntity<>(new AppErrorDto(encryptionFailedException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardException.class)
    public ResponseEntity<AppErrorDto> handleCardLimitException(CardException cardException) {
        return new ResponseEntity<>(new AppErrorDto(cardException.getMessage(),400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ManageYourselfException.class)
    public ResponseEntity<AppErrorDto> handleManageYourselfException(ManageYourselfException manageYourselfException) {
        return new ResponseEntity<>(new AppErrorDto(manageYourselfException.getMessage(),400), HttpStatus.BAD_REQUEST);

    }





}
