package com.example.bankcards.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for errors")
public class AppErrorDto extends ErrorDto {

    @Schema(description = "Error message describing the problem", example = "Invalid request")
    private String message;

    public AppErrorDto(String message, int code) {
        super(code);
        this.message = message;
    }
}
