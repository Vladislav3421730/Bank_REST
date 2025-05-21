package com.example.bankcards.dto.error;

import com.example.bankcards.util.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Data
public abstract class ErrorDto {

    @Schema(description = "Error's code", example = "400")
    private int code;

    @Schema(description = "Timestamp of when the error occurred", example = "2025-02-07 14:30:00")
    private String timestamp;

    public ErrorDto(int code) {
        this.code = code;
        this.timestamp = formatTimestamp();
    }

    private String formatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_PATTERN, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        return sdf.format(new Date());
    }
}
