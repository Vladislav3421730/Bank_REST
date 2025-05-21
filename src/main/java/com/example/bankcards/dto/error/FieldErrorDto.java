package com.example.bankcards.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "DTO for errors with validation")
public class FieldErrorDto extends ErrorDto {

    @Schema(description = "Error map describing the validation")
    private Map<String, String> errors;

    public FieldErrorDto(Map<String, String> errors, int code) {
        super(code);
        this.errors = errors;
    }
}
